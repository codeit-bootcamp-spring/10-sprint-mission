package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = S3BinaryContentStorage.class)
@ActiveProfiles("s3test")
@Tag("integration")
class S3BinaryContentStorageTest {

    private S3BinaryContentStorage storage;

    @BeforeEach
    void setUp() throws IOException {
        System.out.println("Working dir: " + Paths.get(".").toAbsolutePath());

        Properties props = new Properties();
        try (InputStream is = new FileInputStream(".env")) {
            props.load(is);
        }

        storage = new S3BinaryContentStorage(
                props.getProperty("AWS_S3_ACCESS_KEY"),
                props.getProperty("AWS_S3_SECRET_KEY"),
                props.getProperty("AWS_S3_REGION"),
                props.getProperty("AWS_S3_BUCKET"),
                600L,
                null,
                null
        );
    }

    // put: S3에 업로드하고 binaryContentId 반환
    @Test
    void put_test() {
        UUID id = UUID.randomUUID();
        UUID result = storage.put(id, "hello s3".getBytes());
        assertThat(result).isEqualTo(id);
    }

    // get: 업로드한 파일을 InputStream으로 읽기
    @Test
    void get_test() throws Exception {
        UUID id = UUID.randomUUID();
        storage.put(id, "get test content".getBytes());

        InputStream result = storage.get(id);
        assertThat(result.readAllBytes()).isEqualTo("get test content".getBytes());
    }

    // download: 302 리다이렉트와 PresignedUrl 헤더를 반환
    @Test
    void download_test() {
        UUID id = UUID.randomUUID();
        storage.put(id, "download test".getBytes());

        BinaryContentDto dto = new BinaryContentDto(
                id, "test.png", 1024L, "image/png", BinaryContentStatus.SUCCESS);

        ResponseEntity<Void> response = (ResponseEntity<Void>) storage.download(dto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getFirst(HttpHeaders.LOCATION))
                .contains("binary-content/" + id);
    }
}