package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import java.io.InputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.net.URL;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class S3BinaryContentStorageTest {

    @Mock
    private S3Client s3Client;
    @Mock
    private S3Presigner s3Presigner;
    @Mock
    private PresignedGetObjectRequest presignedGetObjectRequest;

    private S3BinaryContentStorage storage;

    @BeforeEach
    void setUp() {
        storage = new S3BinaryContentStorage("dummy-access", "dummy-secret", "ap-northeast-2", "test-bucket");

        // 생성자 내부에서 생성된 실제 AWS 클라이언트 객체를 Mock 객체로 덮어쓰기
        ReflectionTestUtils.setField(storage, "s3Client", s3Client);
        ReflectionTestUtils.setField(storage, "s3Presigner", s3Presigner);
        ReflectionTestUtils.setField(storage, "expiration", 600L);
    }

    @Test
    @DisplayName("파일을 S3에서 성공적으로 Get 한다")
    void get_success() {
        // given
        UUID id = UUID.randomUUID();

        // s3Client가 가짜(Mock) InputStream을 반환하도록 설정
        ResponseInputStream<GetObjectResponse> mockInputStream = mock(ResponseInputStream.class);
        given(s3Client.getObject(any(GetObjectRequest.class))).willReturn(mockInputStream);

        // when
        InputStream result = storage.get(id);

        // then
        assertNotNull(result);
        verify(s3Client).getObject(any(GetObjectRequest.class));
    }

    @Test
    @DisplayName("파일 데이터를 S3에 성공적으로 Put 한다")
    void put_success() {
        // given
        UUID id = UUID.randomUUID();
        byte[] bytes = "test content".getBytes();

        // when
        UUID result = storage.put(id, bytes);

        // then
        assertEquals(id, result);
        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    @DisplayName("파일 다운로드 시 302 FOUND 와 함께 Presigned URL로 리다이렉트 시킨다")
    void download_redirects_with_presigned_url() throws Exception {
        // given
        UUID id = UUID.randomUUID();

        BinaryContentDto dto = mock(BinaryContentDto.class);
        given(dto.id()).willReturn(id);
        given(dto.contentType()).willReturn("image/png");

        String fakeUrl = "https://s3.amazonaws.com/test-bucket/" + id + "?X-Amz-Signature=fake-signature";

        // S3Presigner가 가짜 URL을 반환하도록 설정
        given(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class))).willReturn(presignedGetObjectRequest);
        given(presignedGetObjectRequest.url()).willReturn(new URL(fakeUrl));

        // when
        ResponseEntity<?> response = storage.download(dto);

        // then
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals(fakeUrl, response.getHeaders().getLocation().toString());
    }

    @Test
    @DisplayName("S3 연동 오류 시 파일을 Put 하려고 하면 S3Exception이 발생한다")
    void put_fail_s3_exception() {
        // given
        UUID id = UUID.randomUUID();
        byte[] bytes = "test content".getBytes();

        // s3Client가 업로드 시도 시 AWS 통신 장애나 권한 오류를 던지도록 조작
        given(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
            .willThrow(S3Exception.builder().message("S3 Upload Failed").build());

        // when & then
        // 예외 처리를 따로 감싸지 않았으므로 S3Exception이 그대로 던져지는지 검증
        assertThrows(S3Exception.class, () -> storage.put(id, bytes));
    }

    @Test
    @DisplayName("Presigned URL 발급 중 AWS 연동 오류가 발생하면 S3Exception이 발생한다")
    void download_fail_s3_exception() {
        // given
        UUID id = UUID.randomUUID();
        BinaryContentDto dto = mock(BinaryContentDto.class);
        given(dto.id()).willReturn(id);
        given(dto.contentType()).willReturn("image/png");

        // s3Presigner가 URL 발급 시도 시 예외를 던지도록 조작
        given(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
            .willThrow(S3Exception.builder().message("Presigned URL Generation Failed").build());

        // when & then
        assertThrows(S3Exception.class, () -> storage.download(dto));
    }
}