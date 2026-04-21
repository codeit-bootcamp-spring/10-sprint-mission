package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.exception.file.FileReadFailException;
import com.sprint.mission.discodeit.exception.file.FileSaveFailException;
import com.sprint.mission.discodeit.exception.file.FileUploadFailException;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
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
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.UUID;

@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
@Component
public class S3BinaryContentStorage implements BinaryContentStorage {
    // s3presigner와 s3client는 생산 비용이 비싸므로 한번만 생성
    private final S3Presigner s3Presigner;
    private final S3Client s3Client;
    private final String bucket;

    private final int expireMinutes;

    public S3BinaryContentStorage(
            @Value("${discodeit.storage.s3.access-key}") String accessKey,
            @Value("${discodeit.storage.s3.secret-key}") String secretKey,
            @Value("${discodeit.storage.s3.region}") String region,
            @Value("${discodeit.storage.s3.bucket}") String bucket,
            @Value("${discodeit.storage.s3.presigned-url-expiration}") int expireMinutes
    ) {
        this.bucket = bucket;
        // 공통 자격 증명
        StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey,secretKey)
        );

        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(credentialsProvider)
                .build();

        this.s3Presigner = S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(credentialsProvider)
                .build();

        this.expireMinutes = expireMinutes;
    }

    @Override
    public UUID put(UUID id, byte[] bytes) {
        try{
            String key = id.toString();

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();
            // 로컬 경로로 파일을 찾지말고 바로 s3로 전송
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(bytes));
            return id;
        } catch (Exception e) {
            e.printStackTrace();
            throw new FileUploadFailException();
        }
    }

    @Override
    public InputStream get(UUID id) {
        try {
            String key = id.toString();
            GetObjectRequest getRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();
            return s3Client.getObject(getRequest);
        } catch (Exception e) {
            throw new FileReadFailException(id);
        }
    }

    @Override
    public ResponseEntity<?> download(BinaryContentDto dto) {
        try {
            String presignedUrl = generatePresignedUrl(dto.getId().toString(), dto.getContentType());
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(presignedUrl))
                    .build();
        } catch (Exception e){
            throw new FileSaveFailException(dto.getId());
        }
    }

    // 엑세스키를 사용하여 허용된 시간동안 s3에 접근할 수 있게 해줄 url 반환
    private String generatePresignedUrl(String key, String contentType) {

        // 어떤 파일을 가져올지 요청
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .responseContentType(contentType)
                .build();

        // 얼마나 허용할지 시간을 설정
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                // 유효기간
                .signatureDuration(Duration.ofMinutes(expireMinutes))
                .getObjectRequest(getObjectRequest)
                .build();


        // 문자열로 반환
        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }
}
