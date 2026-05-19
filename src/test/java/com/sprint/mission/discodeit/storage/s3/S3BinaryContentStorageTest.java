package com.sprint.mission.discodeit.storage.s3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.dto.binarycontentdto.BinaryContentDto;
import java.io.IOException;
import java.net.URI;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@ExtendWith(MockitoExtension.class)
class S3BinaryContentStorageTest {

    @Mock
    S3Client s3Client;

    @Mock
    S3Presigner s3Presigner;

    @Mock
    PresignedGetObjectRequest presignedGetObjectRequest;

    S3BinaryContentStorage storage;

    @BeforeEach
    void setUp() {
        storage = new S3BinaryContentStorage("test-bucket", s3Client, s3Presigner);
    }

    @Test
    @DisplayName("S3 업로드 성공! (bytes)")
    void put_success() {
        UUID key = UUID.randomUUID();
        byte[] bytes = "dummy_bytes".getBytes(StandardCharsets.UTF_8);

        storage.put(key, bytes);

        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    @DisplayName("S3 업로드 성공! (Multipart)")
    void put_file_success() throws IOException {
        UUID key = UUID.randomUUID();
        MultipartFile file = new MockMultipartFile(
            "file",
            "dummy.txt",
            "text/plain",
            "dummy_bytes".getBytes(StandardCharsets.UTF_8)
        );

        storage.put(key, file);

        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    @DisplayName("다운로드 성공")
    void download_success() throws MalformedURLException {
        UUID key = UUID.randomUUID();
        String presignedUrl = "https://example.com/download/test.jpg";

        BinaryContentDto binaryContentDto = new BinaryContentDto(
            key,
            "test.jpg",
            1L,
            "image/jpeg",
            "dummy_data".getBytes(StandardCharsets.UTF_8)
        );

        when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
            .thenReturn(presignedGetObjectRequest);
        when(presignedGetObjectRequest.url()).thenReturn(new URL(presignedUrl));

        ResponseEntity<?> response = storage.download(binaryContentDto);

        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals(URI.create(presignedUrl), response.getHeaders().getLocation());
        verify(s3Presigner).presignGetObject(any(GetObjectPresignRequest.class));
    }
}
