package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

@Component
@ConditionalOnProperty(prefix = "discodeit.storage", name = "type", havingValue = "local")
@Slf4j
public class LocalBinaryContentStorage implements BinaryContentStorage {

    private final Path root;

    public LocalBinaryContentStorage(@Value("${discodeit.storage.local.root-path}") String rootPath) {
        this.root = Path.of(rootPath);
    }

    @Override
    public UUID put(UUID binaryContentId, byte[] bytes) {
        // id를 받아 경로 반환 받음
        Path path = resolvePath(binaryContentId);

        try {
            Files.write(path, bytes);
        } catch (IOException e) {
            log.error("[BINARYCONTENT_SAVE_FAIL] 파일 저장 실패: binaryContentId={}", binaryContentId);
            throw new BinaryContentException(
                    ErrorCode.BINARY_CONTENT_CAN_NOT_SAVE,
                    Map.of("binaryContentId", binaryContentId)
            );
        }

        log.info("[BINARYCONTENT_SAVE_SUCCESS] 파일 저장 성공: binaryContentId={}", binaryContentId);
        return binaryContentId;
    }

    @Override
    public InputStream get(UUID binaryContentId) {
        Path path = resolvePath(binaryContentId);

        try {
            return Files.newInputStream(path);
        } catch (IOException e) {
            log.warn("[BINARYCONTENT_GET_FILE_FAIL] 파일 불러오기 실패: binaryContentId={}", binaryContentId);
            throw new BinaryContentException(
                    ErrorCode.BINARY_CONTENT_CAN_NOT_READ,
                    Map.of("binaryContentId", binaryContentId)
            );
        }
    }

    @Override
    public ResponseEntity<?> download(BinaryContentDto dto) {
        InputStream inputStream = get(dto.id());
        // Spring이 파일 응답을 보낼 때 Resource 타입을 사용하면 내부적으로 스트리밍 방식으로 데이터를 내려보내기 편함
        Resource resource = new InputStreamResource(inputStream);

        MediaType mediaType;
        try {
            mediaType = MediaType.parseMediaType(dto.contentType());
        } catch (Exception e) {
            log.warn("[BINARYCONTENT_CONTENT_TYPE_PARSING_FAIL] 파일 contentType 파싱 실패: binaryContentId={}", dto.id());
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }

        log.info("[BINARYCONTENT_DOWNLOAD_SUCCESS] 파일 다운로드 성공: binaryContentId={}", dto.id());
        return ResponseEntity.ok()
                .contentType(mediaType)
                .contentLength(dto.size())
                .header(
                        // 첨부파일이니까 다운로드로 처리하라는 뜻
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + dto.fileName() + "\"")
                .body(resource);
    }

    @PostConstruct  // Bean 생성 시 자동 실행
    private void init() {

        // root 존재 여부 판단
        if (!Files.exists(root)) {
            try {
                Files.createDirectories(root);
            } catch (IOException e) {
                log.error("[BINARYCONTENT_STORAGE_SAVE_FAIL] root 디렉토리 생성 실패로 파일 storage 저장 실패");
                throw new BinaryContentException(
                        ErrorCode.BINARY_CONTENT_FAIL_TO_SAVE_STORAGE,
                        Map.of("storage", "root 디렉토리 생성 실패로 파일 storage 저장 실패")
                );
            }
        }

        // root가 이미 존재하지만 디렉토리가 아닌 경우
        if (Files.exists(root) && !Files.isDirectory(root)) {
            log.error("[BINARYCONTENT_STORAGE_SAVE_FAIL] root가 디렉토리가 아닌 이유로 파일 storage 저장 실패");
            throw new BinaryContentException(
                    ErrorCode.BINARY_CONTENT_FAIL_TO_SAVE_STORAGE,
                    Map.of("storage", "root가 디렉토리가 아닌 이유로 파일 storage 저장 실패")
            );
        }

        // root가 이미 존재하며 디렉토리인 경우
        // 넘어감
    }

    // 저장할 주소를 계산하는 메서드
    private Path resolvePath(UUID id) {
        return root.resolve(id.toString());
    }
}
