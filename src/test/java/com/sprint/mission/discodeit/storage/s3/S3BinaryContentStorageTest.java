package com.sprint.mission.discodeit.storage.s3;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

class S3BinaryContentStorageTest {

    private S3Client s3Client;
    private S3Presigner s3Presigner;
    private S3BinaryContentStorage storage;

    @BeforeEach
    void setUp() throws Exception {
        s3Client = mock(S3Client.class);
        s3Presigner = mock(S3Presigner.class);

        S3Properties properties = new S3Properties(
                "test-access-key", "test-secret-key", "ap-northeast-2", "test-bucket", 600
        );

        storage = new S3BinaryContentStorage(properties);

        // mock 객체로 교체
        var s3ClientField = S3BinaryContentStorage.class.getDeclaredField("s3Client");
        s3ClientField.setAccessible(true);
        s3ClientField.set(storage, s3Client);

        var s3PresignerField = S3BinaryContentStorage.class.getDeclaredField("s3Presigner");
        s3PresignerField.setAccessible(true);
        s3PresignerField.set(storage, s3Presigner);
    }

    @Test
    void upload() {
        UUID id = UUID.randomUUID();
        byte[] data = "Hello, S3!".getBytes(StandardCharsets.UTF_8);
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        UUID result = storage.put(id, data);

        assertThat(result).isEqualTo(id);
        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void download() throws Exception {
        UUID id = UUID.randomUUID();
        byte[] data = "Hello, S3!".getBytes(StandardCharsets.UTF_8);
        ResponseInputStream<GetObjectResponse> responseStream = new ResponseInputStream<>(
                GetObjectResponse.builder().build(),
                new ByteArrayInputStream(data)
        );
        when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(responseStream);

        try (InputStream inputStream = storage.get(id)) {
            byte[] downloaded = inputStream.readAllBytes();
            assertThat(new String(downloaded, StandardCharsets.UTF_8)).isEqualTo("Hello, S3!");
        }
    }

    @Test
    void downloadWithPresignedUrl() throws Exception {
        UUID id = UUID.randomUUID();
        BinaryContentDto metaData = new BinaryContentDto(id, "hello.txt", 10L, "text/plain");

        PresignedGetObjectRequest presigned = mock(PresignedGetObjectRequest.class);
        when(presigned.url()).thenReturn(
                URI.create("https://test-bucket.s3.amazonaws.com/" + id).toURL());
        when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
                .thenReturn(presigned);

        ResponseEntity<?> response = storage.download(metaData);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getFirst(HttpHeaders.LOCATION)).contains(id.toString());
    }
}
