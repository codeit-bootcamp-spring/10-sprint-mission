package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.config.AwsProperties;
import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.exception.etc.S3DownloadException;
import com.sprint.mission.discodeit.exception.etc.S3UploadException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Slf4j
@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
public class S3BinaryContentStorage implements BinaryContentStorage {

    private final String accessKey;
    private final String secretKey;
    private final String region;
    private final String bucket;

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    private final BinaryContentRepository binaryContentRepository;

    @Value("${aws.s3.presigned-url-expiration}")
    private Long presignedUrlExpiration;

    public S3BinaryContentStorage(AwsProperties awsProperties, BinaryContentRepository binaryContentRepository) {
        this.accessKey = awsProperties.getAccessKey();
        this.secretKey = awsProperties.getSecretKey();
        this.region = awsProperties.getRegion();
        this.bucket = awsProperties.getBucket();

        this.s3Client = S3Client.builder()
                .region(Region.of(awsProperties.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(awsProperties.getAccessKey(), awsProperties.getSecretKey())))
                .build();

        this.s3Presigner = S3Presigner.builder()
                .region(Region.of(awsProperties.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(awsProperties.getAccessKey(), awsProperties.getSecretKey())))
                .build();

        this.binaryContentRepository = binaryContentRepository;
    }

    public UUID put(UUID uuid, byte[] bytes) {
        String key = uuid.toString();
        PutObjectRequest putReq = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(getContentType(uuid))
                .build();

        try {
            s3Client.putObject(putReq, RequestBody.fromBytes(bytes));
            log.info("S3 업로드 성공: key={}, size={} bytes", key, bytes.length);
            return uuid;
        } catch (Exception e) {
            throw handleWriteException(uuid, e);
        }
    }

    @Override
    public InputStream get(UUID uuid) {
        String key = uuid.toString();
        GetObjectRequest getReq = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        try {
            InputStream inputStream = s3Client.getObject(getReq);
            log.info("S3 객체 조회 성공: key={}", key);
            return inputStream;
        } catch (Exception e) {
            throw handleReadException(uuid, e);
        }
    }

    @Override
    public ResponseEntity<?> download(BinaryContentDto.Response response) {
        String downloadUrl = generatePresignedUrl(response.id(), response.contentType(), response.fileName());
        log.info("S3 Presigned URL 발행 완료. 리다이렉트 응답을 전송합니다: id={}, fileName={}",
                response.id(), response.fileName());

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(downloadUrl))
                .build();
    }

    private String generatePresignedUrl(UUID uuid, String contentType, String fileName) {
        try {
            String encoded = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                    .replaceAll("\\+", "%20");

            String contentDisposition = String.format(
                    "attachment; filename=\"download\"; filename*=UTF-8''%s",
                    encoded
            );

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(uuid.toString())
                    .responseContentType(contentType)
                    .responseContentDisposition(contentDisposition)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofSeconds(presignedUrlExpiration))
                    .getObjectRequest(getObjectRequest)
                    .build();

            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
            String url = presignedRequest.url().toString();
            log.debug("Presigned URL 생성 완료: key={}", uuid);

            return url;

        } catch (Exception e) {
            throw handlePresignException(uuid, e);
        }
    }

    private String getContentType(UUID uuid) {
        return binaryContentRepository.
                findById(uuid).
                orElseThrow(
                        () -> BinaryContentNotFoundException.withId(uuid))
                .getContentType();
    }

    private RuntimeException handleWriteException(UUID uuid, Exception e) {
        log.error("S3 업로드 실패: key={}, message={}", uuid, e.getMessage());

        // 1. 버킷 없음
        if (e instanceof NoSuchBucketException) {
            return S3UploadException.bucketNotFound(uuid, e);
        }

        // 2. 권한 없음 (403)
        if (e instanceof S3Exception se && se.statusCode() == 403) {
            return S3UploadException.accessDenied(uuid, e);
        }

        // 3. 나머지 (Default)
        return S3UploadException.defaultError(uuid, e);
    }

    private RuntimeException handleReadException(UUID uuid, Exception e) {
        log.error("S3 조회 실패: key={}, message={}", uuid, e.getMessage());

        // 1. 파일 없음 (S3 실물 부재)
        if (e instanceof NoSuchKeyException) {
            return S3DownloadException.fileNotFound(uuid, e);
        }

        // 2. 버킷 없음
        if (e instanceof NoSuchBucketException) {
            return S3DownloadException.bucketNotFound(uuid, e);
        }

        // 3. 권한 없음 및 기타 S3 에러 확인
        if (e instanceof S3Exception se) {
            if (se.statusCode() == 403) {
                return S3DownloadException.accessDenied(uuid, e);
            }
            if (se.statusCode() == 404) {
                return S3DownloadException.fileNotFound(uuid, e);
            }
        }

        // 4. 나머지 (Default)
        return S3DownloadException.defaultError(uuid, e);
    }

    private RuntimeException handlePresignException(UUID uuid, Exception e) {
        log.error("S3 Presigned URL 생성 실패: key={}, message={}", uuid, e.getMessage());
        return S3DownloadException.presignError(uuid, e);
    }
}
