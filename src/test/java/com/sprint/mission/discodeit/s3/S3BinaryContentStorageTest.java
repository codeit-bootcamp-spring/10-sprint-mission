package com.sprint.mission.discodeit.s3;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.storage.s3.S3BinaryContentStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class S3BinaryContentStorageTest {

    private S3BinaryContentStorage storage;

    @BeforeEach
    void setUp() throws Exception {
        Properties props = new Properties();

        try (InputStream inputStream = java.nio.file.Files.newInputStream(java.nio.file.Path.of(".env"));
             java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(inputStream))) {

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                int idx = line.indexOf('=');
                if (idx == -1) {
                    continue;
                }

                String key = line.substring(0, idx).trim();
                String value = line.substring(idx + 1).trim();
                props.setProperty(key, value);
            }
        }

        storage = new S3BinaryContentStorage(
                props.getProperty("AWS_S3_ACCESS_KEY"),
                props.getProperty("AWS_S3_SECRET_KEY"),
                props.getProperty("AWS_S3_REGION"),
                props.getProperty("AWS_S3_BUCKET"),
                Long.parseLong(props.getProperty("AWS_S3_PRESIGNED_URL_EXPIRATION", "600"))
        );
    }

    @Test
    @DisplayName("put과 get이 동작한다")
    void putAndGetTest() throws Exception {
        UUID id = UUID.randomUUID();
        String content = "hello s3";

        storage.put(id, content.getBytes(StandardCharsets.UTF_8));

        InputStream inputStream = storage.get(id);
        assertNotNull(inputStream);

        String result = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        assertEquals(content, result);
    }

    @Test
    @DisplayName("download는 presigned url redirect 응답을 반환한다")
    void downloadTest() {
        UUID id = UUID.randomUUID();
        storage.put(id, "hello".getBytes(StandardCharsets.UTF_8));

        BinaryContentDto dto = new BinaryContentDto(
                id,
                "test.txt",
                5L,
                "text/plain"
        );

        ResponseEntity<?> response = storage.download(dto);

        assertTrue(response.getStatusCode().is3xxRedirection());
        assertNotNull(response.getHeaders().getLocation());
    }
}
