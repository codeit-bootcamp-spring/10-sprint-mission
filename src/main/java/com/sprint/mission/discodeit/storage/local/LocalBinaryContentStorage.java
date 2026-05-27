package com.sprint.mission.discodeit.storage.local;


import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentReadFailedException;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentSaveFailedException;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentStorageInitFailedException;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
public class LocalBinaryContentStorage implements BinaryContentStorage {
    private final Path root;

    public LocalBinaryContentStorage(@Value("${discodeit.storage.local.root-path}") String root) {
        this.root = Path.of(root);
    }

    /**
     * 루트 디렉토리 초기화 <br>
     * Bean이 생성되면 자동으로 호출
     */
    @PostConstruct
    void init() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new BinaryContentStorageInitFailedException(e);
        }
    }

    /**
     * 파일의 실제 저장 위치에 대한 규칙을 정의 <br>
     * 파일 저장 위치 규칙 예시 : {@code {root}/{UUID}} <br>
     * {@code put}, {@code get} 메서드에서 호출해 일관된 파일 경로 규칙을 유지
     * @param binaryContentId
     * @return {@code Path}
     */
    public Path resolvePath(UUID binaryContentId) {
        // root 하위 경로로 새로운 Path를 만듬
        // root = "/storage/binary" 이면 "/storage/binary/UUID.toString"이 만들어짐
        return root.resolve(binaryContentId.toString());
    }

    @Override
    public UUID put(UUID binaryContentId, byte[] bytes) {
        Path path = resolvePath(binaryContentId);

        try {
            Files.write(path, bytes);
        } catch (IOException e) {
            throw new BinaryContentSaveFailedException(binaryContentId, e);
        }

        return binaryContentId;
    }

    @Override
    public InputStream get(UUID binaryContentId) {
        Path path = resolvePath(binaryContentId);

        try {
            // 파일을 읽을 수 있는 통로를 연다.
            return Files.newInputStream(path);
        } catch(IOException e) {
            throw new BinaryContentReadFailedException(binaryContentId, e);
        }
    }

    @Override
    public ResponseEntity<Resource> download(BinaryContentDto binaryContentDto) {
        // Resource 타입 : Spring에서 파일, 스트림 같은 데이터 자원을 표현하는 타입

        // InputStream :  바이트 데이터를 읽기 위한 통로
        InputStream inputStream = get(binaryContentDto.id());

        // InputStreamResource : Resource의 구현체 중 하나로
        // 이미 얻어온 InputStream을 Spring이 처리하기 쉬운 Resource 형태로 감싼다.
        // Spring은 ResponseEntity의 body로 파일/스트림 같은 자원을 보낼 때 Resource 타입을 잘 처리한다.
        Resource resource = new InputStreamResource(inputStream);

        return ResponseEntity.status(HttpStatus.OK)
                // Content-Type 헤더 설정
                // MediaType.parseMediaType(type) : type(`image/png` 등)을 Spring의 MediaType 객체로 변경
                .contentType(MediaType.parseMediaType(binaryContentDto.contentType()))
                // Content-Length 헤더 설정
                // 응답 본문 크기가 몇 바이트인지 알려주는 헤더
                .contentLength(binaryContentDto.size())
                .header(
                        // Content-Disposition 헤더 설정
                        // 브라우저에서 이 응답을 첨부파일처럼 다운로드하라고 알려줌
                        HttpHeaders.CONTENT_DISPOSITION,
                        // ContentDisposition 객체 생성
                        ContentDisposition.attachment() // ContentDisposition 빌더
                                // attachment()` : 브라우저가 응답을 화면에 바로 표시하기보다 다운로드 대상으로 처리하도록 유도
                                .filename(binaryContentDto.fileName()) // 파일명 설정
                                .build() // 객체 완성
                                .toString() // HTTP 헤더 값은 문자열이어야 하기 때문에 문자열로 변환
                )
                .body(resource);
    }
}
