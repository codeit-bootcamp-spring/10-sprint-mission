package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import software.amazon.awssdk.core.sync.RequestBody;

@ExtendWith(MockitoExtension.class)
class S3BinaryContentStorageTest {

    private S3BinaryContentStorage storage;

    @Mock
    private S3Client mockS3Client;

    @BeforeEach
    void setUp() {
        storage = spy(new S3BinaryContentStorage(
                "accessKey",
                "secretKey",
                "ap-northeast-2",
                "test-bucket"
        ));

        // 핵심: s3Client()를 mock으로 강제 변경
        doReturn(mockS3Client).when(storage).s3Client();
    }

    @Test
    @DisplayName("이미지파일을 S3에 업로드할 수 있어야한다.")
    void profile_upload_success_on_s3() {
        // given
        UUID id = UUID.randomUUID();
        byte[] bytes = new byte[]{1, 2, 3};
        String contentType = "image/jpeg";

        // when
        UUID result = storage.put(id, bytes, contentType);

        // then
        assertEquals(id, result);

        // putObject 호출 검증
        ArgumentCaptor<PutObjectRequest> captor =
                ArgumentCaptor.forClass(PutObjectRequest.class);

        verify(mockS3Client).putObject(captor.capture(), any(RequestBody.class));

        PutObjectRequest request = captor.getValue();

        assertEquals("test-bucket", request.bucket());
        assertEquals(id.toString(), request.key());
        assertEquals(contentType, request.contentType());
    }

    @Test
    @DisplayName("버킷이 없는경우(null값인 경우) S3업로드 실패가 되어야한다.")
    void s3_upload_fail_when_bucket_null(){

        // given
        S3BinaryContentStorage storage = new S3BinaryContentStorage(
                "test-key",
                "test-secret",
                "ap-northeast-2",
                null

        );
        UUID id = UUID.randomUUID();
        byte[] bytes = new byte[]{1,2,3};

        // when, then
        assertThrows(RuntimeException.class, () -> storage.put(id,bytes, "image/jpeg"));

    }

}