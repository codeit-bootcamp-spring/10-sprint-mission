package com.sprint.mission.discodeit.storage;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

import com.sprint.mission.discodeit.dto.binarycontentdto.BinaryContentDto;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
public class LocalBinaryContentStorage implements BinaryContentStorage {

    private final Path root;

    // application.yaml에서 root 값을 주입받음(.discodeit)
    public LocalBinaryContentStorage(
        @Value("${discodeit.storage.local.root-path}") String rootPath
    ) {
        this.root = Path.of(rootPath);
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to initialize storage root: " + root, e);
        }
    }


    @Override
    public UUID put(UUID uuid, byte[] bytes) {
        // 파일 업로드 메서드 시작 로그
        log.trace("파일 업로드 메서드 시작: id={}", uuid);

        Path path = resolvePath(uuid); // resolvePath 메서드를 통해 저장할 경로를 받아옴.

        try {
            // 받아온 경로를 바탕으로 파일 쓰기 실행
            // 파일이 존재하면 덮어씀
            // 파일이 존재하지 않으면 새로 생성
            Files.write(path, bytes, CREATE, TRUNCATE_EXISTING, StandardOpenOption.WRITE);
            log.info("파일 업로드 완료: id={}", uuid);
            return uuid;
        } catch (IOException e) {
            // 런타임 에러로 반환하여 던짐.
            throw new IllegalStateException("Failed to write binary content: " + uuid,
                e); // exception wrapping
        }
    }

    @Override
    public InputStream get(UUID uuid) {
        Path path = resolvePath(uuid); // 가져올 파일의 경로를 받아옴.
        try {
            return Files.newInputStream(path); // path에 존재하는 파일을 읽고 InputStream을 반환한다.
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read binary content: " + uuid,
                e); // exception wrapping
        }
    }

    // 다운로드 메서드
    @Override
    public ResponseEntity<Resource> download(BinaryContentDto binaryContentDto) {
        // 다운로드 메서드 시작 로그
        log.trace("다운로드 시작: binaryContentDtoId={}", binaryContentDto.id());

        // 다운로드 하고자 하는 첨부 파일 dto에서 byte를 추출
        byte[] bytes = binaryContentDto.bytes();

        // Dto에 byte가 null이거나 길이가 0이면. 즉, byte가 존재하지 않는다면
        if (bytes == null || bytes.length == 0) {
            // Dto에 있는 id를 바탕으로 해당 path에 있는 파일을 읽고 InputStream 추출
            try (InputStream in = get(binaryContentDto.id())) {
                bytes = in.readAllBytes(); // InputStream에서 bytes를 추출

                // 만약 해당 path에 파일이 존재하지 않으면 예외 발생
            } catch (IOException e) {
                throw new IllegalStateException(
                    "Failed to build download response for: " + binaryContentDto.id(), e);
            }
        }

        // Resource는 Spring에서 파일 응답을 처리할 때 사용하는 타입
        // InputStream을 Spring이 HTTP 응답으로 보낼 수 있도록 Resource로 감쌈
        Resource resource = new ByteArrayResource(bytes);
        MediaType mediaType;

        // Content-Type 헤더 설정을 위해 MediaType 설정
        // Dto에 MediaType 정보 존재 시, 해당 정보로 설정
        // Dto에 MediaType 미존재 시, OCTET_STREAM으로 설정.
        mediaType =
            (binaryContentDto.contentType() != null && !binaryContentDto.contentType().isBlank())
                ? MediaType.parseMediaType(binaryContentDto.contentType())
                : MediaType.APPLICATION_OCTET_STREAM;

//        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM; // 다운로드를 위해서 MediaType을 OCTET_STREAM으로 설정
//
//        if (binaryContentDto.contentType() != null && !binaryContentDto.contentType().isBlank()) {
//            mediaType = MediaType.parseMediaType(binaryContentDto.contentType());
//        }

        // 파일 이름을 결정함.
        // dto에 파일 이름이 존재하면 dto를 따라서,
        // dto에 파일 이름이 존재하지 않는다면 id 기반 파일 이름으로 결정.
        String fileName =
            binaryContentDto.fileName() != null && !binaryContentDto.fileName().isBlank()
                ? binaryContentDto.fileName()
                : binaryContentDto.id().toString();
        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.ok()
            .contentType(mediaType)
            .contentLength(bytes.length)
            .header(HttpHeaders.CACHE_CONTROL, "no-store");

        if (!mediaType.getType().equals("image")) {
            responseBuilder.header(
                HttpHeaders.CONTENT_DISPOSITION,
                ContentDisposition.attachment().filename(fileName).build().toString()
            );
        }

        ResponseEntity<Resource> response = responseBuilder.body(resource);

        log.info("다운로드 완료: fileName={}", fileName);

        return response;
    }

    // 저장할 주소를 계산하는 메소드
    Path resolvePath(UUID uuid) {
        return root.resolve(uuid.toString());
    }
}
