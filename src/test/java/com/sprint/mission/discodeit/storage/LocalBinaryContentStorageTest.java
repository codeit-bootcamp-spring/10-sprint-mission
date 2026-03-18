package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.response.ApiException;
import com.sprint.mission.discodeit.response.ErrorCode;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class LocalBinaryContentStorageTest {

    @Autowired
    private BinaryContentStorage binaryContentStorage;

    @Value("${discodeit.storage.local.root-path}")
    private Path rootPath;

    @BeforeEach
    void setUp() throws IOException {
        if (Files.exists(rootPath)) {
            Files.walk(rootPath)
                    .sorted(Comparator.reverseOrder())
                    .filter(path -> !path.equals(rootPath))
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }

        Files.createDirectories(rootPath);
    }

    @Test
    @DisplayName("로컬 스토리지 put/get 성공")
    void putAndGet_success() throws IOException {
        UUID id = UUID.randomUUID();
        byte[] expected = "hello-storage".getBytes();

        UUID savedId = binaryContentStorage.put(id, expected);

        try (InputStream inputStream = binaryContentStorage.get(savedId)) {
            byte[] actual = inputStream.readAllBytes();
            assertThat(actual).isEqualTo(expected);
        }
    }

    @Test
    @DisplayName("존재하지 않는 파일 get 시 예외")
    void get_fail_notFound() {
        UUID id = UUID.randomUUID();

        assertThatThrownBy(() -> binaryContentStorage.get(id))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> {
                    ApiException apiException = (ApiException) ex;
                    assertThat(apiException.getErrorCode()).isEqualTo(ErrorCode.BINARY_CONTENT_NOT_FOUND);
                });
    }

    @Test
    @DisplayName("다운로드 응답 생성 성공")
    void download_success() throws IOException {
        UUID id = UUID.randomUUID();
        byte[] bytes = "download-data".getBytes();
        binaryContentStorage.put(id, bytes);

        BinaryContentDto dto = new BinaryContentDto(
                id,
                "sample.png",
                (long) bytes.length,
                "image/png"
        );

        ResponseEntity<Resource> response = binaryContentStorage.download(dto);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE)).isEqualTo("image/png");
        assertThat(response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION))
                .contains("attachment")
                .contains("sample.png");

        Resource body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.contentLength()).isEqualTo(bytes.length);

        try (InputStream inputStream = body.getInputStream()) {
            assertThat(inputStream.readAllBytes()).isEqualTo(bytes);
        }
    }
}
