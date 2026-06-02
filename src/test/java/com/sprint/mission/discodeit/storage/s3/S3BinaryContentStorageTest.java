package com.sprint.mission.discodeit.storage.s3;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

import com.sprint.mission.discodeit.config.AwsProperties;
import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.etc.S3DownloadException;
import com.sprint.mission.discodeit.exception.etc.S3UploadException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("S3BinaryContentStorage 통합테스트(실제 s3와 통신함)")
class S3BinaryContentStorageTest {

    @Autowired
    private S3BinaryContentStorage storage;

    @MockitoBean
    private BinaryContentRepository repository;

    @Autowired
    private AwsProperties awsProperties;

    private S3Client s3Client;
    private S3Presigner s3Presigner;

    private UUID currentTestId; // 테스트 완료 후 삭제를 위한 추적 ID

    @BeforeEach
    void setUp() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                awsProperties.getAccessKey(), awsProperties.getSecretKey());

        s3Client = S3Client.builder()
                .region(Region.of(awsProperties.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();

        s3Presigner = S3Presigner.builder()
                .region(Region.of(awsProperties.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    @AfterEach
    void tearDown() {
        if (currentTestId != null) {
            try {
                s3Client.deleteObject(b -> b.bucket(awsProperties.getBucket()).key(currentTestId.toString()));
            } catch (Exception e) {
                // 삭제 실패 시 무시
            }
            currentTestId = null;
        }
    }

    @Nested
    @DisplayName("파일 업로드 (put) 테스트")
    class Put {
        
        @Test
        @DisplayName("S3에 바이너리 데이터를 업로드 성공")
        void put_Success() {
            // Given
            UUID id = UUID.randomUUID();
            byte[] data = "TDD 시작!".getBytes();
            BinaryContent content = new BinaryContent("test.txt", "text/plain", data.length);
            
            given(repository.findById(id)).willReturn(Optional.of(content));

            // When
            UUID returnedId = storage.put(id, data);
            currentTestId = returnedId;

            // Then
            assertNotNull(returnedId);
            assertEquals(id, returnedId);
            
            // 검증: 실제 S3에 업로드되었는지 확인
            ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(b -> b.bucket(awsProperties.getBucket()).key(returnedId.toString()));
            assertNotNull(s3Object);
        }

        @Test
        @DisplayName("S3에 업로드할 때 버킷이 없는 경우 (NoSuchBucketException)")
        void put_BucketNotFound() {
            // Given
            UUID id = UUID.randomUUID();
            byte[] data = "test data".getBytes();
            BinaryContent content = new BinaryContent("test.txt", "text/plain", data.length);
            given(repository.findById(id)).willReturn(Optional.of(content));

            String originalBucket = awsProperties.getBucket();
            ReflectionTestUtils.setField(storage, "bucket", "invalid-bucket-" + UUID.randomUUID());

            try {
                // When & Then
                S3UploadException exception = assertThrows(S3UploadException.class, () -> storage.put(id, data));
                assertEquals(ErrorCode.S3_UPLOAD_BUCKET_NOT_FOUND, exception.getErrorCode());
                assertEquals(id.toString(), exception.getDetails().get("fileKey"));
            } finally {
                ReflectionTestUtils.setField(storage, "bucket", originalBucket);
            }
        }

        @Test
        @DisplayName("S3에 업로드할 때 권한이 없는 경우 (S3Exception 403)")
        void put_AccessDenied() {
            // Given
            UUID id = UUID.randomUUID();
            byte[] data = "test data".getBytes();
            BinaryContent content = new BinaryContent("test.txt", "text/plain", data.length);
            given(repository.findById(id)).willReturn(Optional.of(content));

            S3Client badS3Client = S3Client.builder()
                .region(Region.of(awsProperties.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("BAD_KEY", "BAD_SECRET")))
                .build();
            S3Client originalS3Client = (S3Client) ReflectionTestUtils.getField(storage, "s3Client");
            ReflectionTestUtils.setField(storage, "s3Client", badS3Client);

            try {
                // When & Then
                S3UploadException exception = assertThrows(S3UploadException.class, () -> storage.put(id, data));
                assertEquals(ErrorCode.S3_UPLOAD_ACCESS_DENIED, exception.getErrorCode());
                assertEquals(id.toString(), exception.getDetails().get("fileKey"));
            } finally {
                ReflectionTestUtils.setField(storage, "s3Client", originalS3Client);
                badS3Client.close();
            }
        }

        @Test
        @DisplayName("S3에 업로드할 때 알 수 없는 S3 에러 발생 (기본 예외)")
        void put_DefaultError() {
            // Given
            UUID id = UUID.randomUUID();
            byte[] data = null; // null 데이터를 전달하여 NullPointerException (기본 예외) 유도
            BinaryContent content = new BinaryContent("test.txt", "text/plain", 10);
            given(repository.findById(id)).willReturn(Optional.of(content));

            // When & Then
            S3UploadException exception = assertThrows(S3UploadException.class, () -> storage.put(id, data));
            assertEquals(ErrorCode.S3_UPLOAD_ERROR, exception.getErrorCode());
            assertEquals(id.toString(), exception.getDetails().get("fileKey"));
        }
    }

    @Nested
    @DisplayName("파일 조회(get) 테스트")
    class Get {
        
        @Test
        @DisplayName("S3에서 객체를 정상적으로 조회 성공")
        void get_Success() {
            // Given
            UUID id = UUID.randomUUID();
            byte[] mockData = "S3 Get Test".getBytes();
            currentTestId = id;
            
            // 실제 S3에 데이터를 미리 업로드해둠
            s3Client.putObject(b -> b.bucket(awsProperties.getBucket()).key(id.toString()), RequestBody.fromBytes(mockData));

            // When
            InputStream resultStream = storage.get(id);

            // Then
            assertNotNull(resultStream);
            assertDoesNotThrow(resultStream::close);
        }

        @Test
        @DisplayName("S3 조회 시 파일이 없는 경우 (NoSuchKeyException)")
        void get_FileNotFound() {
            // Given
            UUID id = UUID.randomUUID(); // 업로드하지 않은 임의의 ID

            // When & Then
            S3DownloadException exception = assertThrows(S3DownloadException.class, () -> storage.get(id));
            assertEquals(ErrorCode.S3_DOWNLOAD_FILE_NOT_FOUND, exception.getErrorCode());
            assertEquals(id.toString(), exception.getDetails().get("fileKey"));
        }

        @Test
        @DisplayName("S3 조회 시 권한이 없는 경우 (S3Exception 403)")
        void get_AccessDenied() {
            // Given
            UUID id = UUID.randomUUID();

            S3Client badS3Client = S3Client.builder()
                .region(Region.of(awsProperties.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("BAD_KEY", "BAD_SECRET")))
                .build();
            S3Client originalS3Client = (S3Client) ReflectionTestUtils.getField(storage, "s3Client");
            ReflectionTestUtils.setField(storage, "s3Client", badS3Client);

            try {
                // When & Then
                S3DownloadException exception = assertThrows(S3DownloadException.class, () -> storage.get(id));
                assertEquals(ErrorCode.S3_DOWNLOAD_ACCESS_DENIED, exception.getErrorCode());
                assertEquals(id.toString(), exception.getDetails().get("fileKey"));
            } finally {
                ReflectionTestUtils.setField(storage, "s3Client", originalS3Client);
                badS3Client.close();
            }
        }

        @Test
        @DisplayName("S3 조회 시 파일이 없는 경우 (S3Exception 404)")
        void get_FileNotFound_404() {
            // S3 실제 통신 시 404는 보통 NoSuchKeyException으로 발생하므로,
            // get_FileNotFound와 동일한 방식으로 실제 없는 파일을 요청하여 404/NoSuchKey 처리를 검증
            // Given
            UUID id = UUID.randomUUID();

            // When & Then
            S3DownloadException exception = assertThrows(S3DownloadException.class, () -> storage.get(id));
            assertEquals(ErrorCode.S3_DOWNLOAD_FILE_NOT_FOUND, exception.getErrorCode());
            assertEquals(id.toString(), exception.getDetails().get("fileKey"));
        }

        @Test
        @DisplayName("S3 조회 시 버킷이 없는 경우 (NoSuchBucketException)")
        void get_BucketNotFound() {
            // Given
            UUID id = UUID.randomUUID();
            String originalBucket = awsProperties.getBucket();
            ReflectionTestUtils.setField(storage, "bucket", "invalid-bucket-" + UUID.randomUUID());

            try {
                // When & Then
                S3DownloadException exception = assertThrows(S3DownloadException.class, () -> storage.get(id));
                assertEquals(ErrorCode.S3_DOWNLOAD_BUCKET_NOT_FOUND, exception.getErrorCode());
                assertEquals(id.toString(), exception.getDetails().get("fileKey"));
            } finally {
                ReflectionTestUtils.setField(storage, "bucket", originalBucket);
            }
        }
    }

    @Nested
    @DisplayName("다운로드 URL 생성 (download) 테스트")
    class Download {
        
        @Test
        @DisplayName("S3 Presigned URL을 통한 다운로드 응답 리다이렉트 생성 성공")
        void download_Success() {
            // Given
            UUID id = UUID.randomUUID();
            BinaryContentDto.Response response = new BinaryContentDto.Response(
                    id, "test-download.txt", 100L, "text/plain"
            );
            
            // When
            ResponseEntity<?> result = storage.download(response);

            // Then
            assertNotNull(result);
            assertEquals(HttpStatus.FOUND, result.getStatusCode());
            assertNotNull(result.getHeaders().getLocation());
            assertTrue(result.getHeaders().getLocation().toString().contains(id.toString()));
            assertTrue(result.getHeaders().getLocation().toString().contains("X-Amz-Signature"));
        }

        @Test
        @DisplayName("Presigned URL 생성 중 S3 관련 예외 발생 시 S3DownloadException (PRESIGN_ERROR) 발생")
        void download_PresignError() {
            // Given
            UUID id = UUID.randomUUID();
            BinaryContentDto.Response response = new BinaryContentDto.Response(
                    id, "error.txt", 0L, "text/plain"
            );

            // bucket을 null로 설정하여 IllegalArgumentException 유도
            String originalBucket = awsProperties.getBucket();
            ReflectionTestUtils.setField(storage, "bucket", null);

            try {
                // When & Then
                S3DownloadException exception = assertThrows(S3DownloadException.class, () -> storage.download(response));
                assertEquals(ErrorCode.S3_DOWNLOAD_PRESIGN_ERROR, exception.getErrorCode());
                assertEquals(id.toString(), exception.getDetails().get("fileKey"));
            } finally {
                ReflectionTestUtils.setField(storage, "bucket", originalBucket);
            }
        }
    }
}