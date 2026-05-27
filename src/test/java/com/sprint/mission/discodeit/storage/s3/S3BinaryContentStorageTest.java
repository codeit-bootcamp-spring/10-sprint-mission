package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.config.aws.AwsProperties;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.exception.binarycontent.AwsServerConnectFailedException;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentReadFailedException;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentSaveFailedException;
import com.sprint.mission.discodeit.exception.binarycontent.PresignedUrlCreateFailedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
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
    private AwsProperties awsProperties;

    @InjectMocks
    private S3BinaryContentStorage s3BinaryContentStorage;

    @Nested
    @DisplayName("S3에 파일 업로드 테스트")
    class uploadFile {

        @Test
        @DisplayName("S3에 파일 업로드 성공")
        void success_put_to_s3() {
            // given(준비)
            UUID binaryContentId = UUID.randomUUID();
            byte[] bytes = "test".getBytes();

            given(awsProperties.getBucket()).willReturn("test-bucket");

            // when(실행)
            UUID result = s3BinaryContentStorage.put(binaryContentId, bytes);

            // then(검증)
            assertEquals(binaryContentId, result);

            verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        }

        @Test
        @DisplayName("S3에서 파일 업로드 실패 시 BinaryContentSaveFailedException 예외 발생")
        void fail_put_to_s3_when_upload_failed() {
            // given(준비)
            UUID binaryContentId = UUID.randomUUID();
            byte[] bytes = "test".getBytes();

            given(awsProperties.getBucket()).willReturn("test-bucket");
            given(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                    .willThrow(S3Exception.builder().message("S3 Upload Failed").build());

            // when(실행), then(검증)
            assertThrows(BinaryContentSaveFailedException.class,
                    () -> s3BinaryContentStorage.put(binaryContentId, bytes));

            verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        }

        @Test
        @DisplayName("AWS에 연결 실패 시 AwsServerConnectFailedException 예외 발생")
        void fail_put_to_s3_when_aws_connect_failed() {
            // given(준비)
            UUID binaryContentId = UUID.randomUUID();
            byte[] bytes = "test".getBytes();

            given(awsProperties.getBucket()).willReturn("test-bucket");
            given(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                    .willThrow(SdkClientException.builder().message("AWS Connect failed").build());

            // when(실행), then(검증)
            assertThrows(AwsServerConnectFailedException.class,
                    ()-> s3BinaryContentStorage.put(binaryContentId, bytes));

            verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        }
    }

    @Nested
    @DisplayName("S3에서 파일 정보 조회 테스트")
    class getFile {

        @Test
        @DisplayName("S3에서 파일 정보 조회 성공")
        void success_get_to_s3() {
            // given(준비)
            UUID binaryContentId = UUID.randomUUID();
            ResponseInputStream responseInputStream = mock(ResponseInputStream.class);

            given(awsProperties.getBucket()).willReturn("test-bucket");
            given(s3Client.getObject(any(GetObjectRequest.class))).willReturn(responseInputStream);

            // when(실행)
            InputStream result = s3BinaryContentStorage.get(binaryContentId);

            // then(검증)
            assertEquals(responseInputStream, result);

            verify(s3Client).getObject(any(GetObjectRequest.class));
        }

        @Test
        @DisplayName("S3에서 파일 조회 실패 시 BinaryContentReadFailedException 예외 발생")
        void fail_get_to_s3_when_upload_failed() {
            // given(준비)
            UUID binaryContentId = UUID.randomUUID();

            given(awsProperties.getBucket()).willReturn("test-bucket");
            given(s3Client.getObject(any(GetObjectRequest.class)))
                    .willThrow(S3Exception.builder().message("S3 Read Failed").build());

            // when(실행), then(검증)
            assertThrows(BinaryContentReadFailedException.class,
                    ()-> s3BinaryContentStorage.get(binaryContentId));

            verify(s3Client).getObject(any(GetObjectRequest.class));
        }

        @Test
        @DisplayName("AWS에 연결 실패 시 AwsServerConnectFailedException 예외 발생")
        void fail_get_to_s3_when_aws_connect_failed() {
            // given(준비)
            UUID binaryContentId = UUID.randomUUID();

            given(awsProperties.getBucket()).willReturn("test-bucket");
            given(s3Client.getObject(any(GetObjectRequest.class)))
                    .willThrow(SdkClientException.builder().message("AWS Connect failed").build());

            // when(실행), then(검증)
            assertThrows(AwsServerConnectFailedException.class,
                    ()-> s3BinaryContentStorage.get(binaryContentId));

            verify(s3Client).getObject(any(GetObjectRequest.class));
        }
    }

    private BinaryContentDto createBinaryContentDto(String fileName, String contentType, Long size) {
        UUID binaryContentId = UUID.randomUUID();
        return new BinaryContentDto(binaryContentId, fileName, size, contentType);
    }

    @Nested
    @DisplayName("S3에서 파일 다운로드 테스트")
    class downloadFile {

        @Test
        @DisplayName("S3에서 파일 다운로드 성공")
        void success_download_to_s3() throws MalformedURLException, URISyntaxException {
            // given(준비)
            BinaryContentDto  binaryContentDto = createBinaryContentDto("testFile", "contentType", 10L);
            PresignedGetObjectRequest presignedGetObjectRequest = mock(PresignedGetObjectRequest.class);

            given(awsProperties.getBucket()).willReturn("test-bucket");
            given(awsProperties.getPresignedUrlExpiration()).willReturn(600L);
            given(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class))).willReturn(presignedGetObjectRequest);
            given(presignedGetObjectRequest.url()).willReturn(new URL("http://test.com/s3download"));

            // when(실행)
            ResponseEntity<Void> result = s3BinaryContentStorage.download(binaryContentDto);

            // then(검증)
            verify(s3Presigner).presignGetObject(any(GetObjectPresignRequest.class));

            assertEquals(HttpStatus.FOUND, result.getStatusCode());
            assertEquals(new URI("http://test.com/s3download"), result.getHeaders().getLocation());
        }

        @Test
        @DisplayName("S3에서 파일 다운로드 실패 시 PresignedUrlCreateFailedException 예외 발생")
        void fail_download_to_s3_when_download_fail() {
            // given(준비)
            BinaryContentDto  binaryContentDto = createBinaryContentDto("testFile", "contentType", 10L);

            given(awsProperties.getBucket()).willReturn("test-bucket");
            given(awsProperties.getPresignedUrlExpiration()).willReturn(600L);
            given(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
                    .willThrow(S3Exception.builder().message("Presigned Url Create Failed").build());

            // when(실행), then(검증)
            assertThrows(PresignedUrlCreateFailedException.class,
                    ()-> s3BinaryContentStorage.download(binaryContentDto));

            verify(s3Presigner).presignGetObject(any(GetObjectPresignRequest.class));
        }

        @Test
        @DisplayName("AWS에 연결 실패 시 AwsServerConnectFailedException 예외 발생")
        void fail_download_to_s3_when_aws_connect_failed() {
            // given(준비)
            BinaryContentDto  binaryContentDto = createBinaryContentDto("testFile", "contentType", 10L);

            given(awsProperties.getBucket()).willReturn("test-bucket");
            given(awsProperties.getPresignedUrlExpiration()).willReturn(600L);
            given(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
                    .willThrow(SdkClientException.builder().message("AWS Connect failed").build());

            // when(실행), then(검증)
            assertThrows(AwsServerConnectFailedException.class,
                    ()-> s3BinaryContentStorage.download(binaryContentDto));

            verify(s3Presigner).presignGetObject(any(GetObjectPresignRequest.class));
        }
    }
}