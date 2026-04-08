package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.InputStream;
import java.time.Duration;
import java.util.UUID;

//discodeit.storage.type 값이 s3인 경우에만 Bean으로 등록 되어야한다.
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
@Component
//파일 저장방식을 s3로 구현
public class S3BinaryContentStorage implements BinaryContentStorage {



    private final String accessKey;
    private final String secretKey;
    private final String region;
    private final String bucket;

    public S3BinaryContentStorage(
            @Value("${discodeit.storage.s3.access-key}") String accessKey,
            @Value("${discodeit.storage.s3.secret-key}") String secretKey,
            @Value("${discodeit.storage.s3.region}") String region,
            @Value("${discodeit.storage.s3.bucket}") String bucket
    ) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.region = region;
        this.bucket = bucket;
    }

    //s3에 업로드
    //ex) binaryContentId: abc123, bytes: [104, 101, 108, ...]
    @Override
    public UUID put(UUID binaryContentId, byte[] bytes, String contentType) {

        try {
            // 1. 키 생성
            // 키생성 규칙없이 UUID로 저장
            String key = binaryContentId.toString();

            // 2. PutObjectRequest 생성
            // bucket이라는 이름안에 key이름으로 파일 저장할게.
            PutObjectRequest putReq = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(contentType)
                    .build();

            //3. 업로드
            s3Client().putObject(putReq, RequestBody.fromBytes(bytes));
            return binaryContentId;

        } catch (Exception e){
            throw new RuntimeException("S3 업로드 실패", e);
        }

    }

    //s3에 저장된 파일을 서버로 가져오는 메서드.
    //InputStream 형태로 파일 데이터를 읽어오는 역할
    @Override
    public InputStream get(UUID binaryContentId) {
        GetObjectRequest getReq = GetObjectRequest.builder()
                .bucket(bucket)
                .key(binaryContentId.toString())
                .build();
        return s3Client().getObject(getReq);
    }

    //클라이언트를 S3로 직접 보내는 방식
    @Override
    public ResponseEntity<Void> download(BinaryContentDto metaData) {

        //S3 접근 가능한 임시 URL 생성
        String presignedUrl = generatePresignedUrl(
                metaData.id().toString(),
                metaData.contentType(),
                metaData.fileName()
        );

        return ResponseEntity
                .status(HttpStatus.FOUND)   // 302 리다이렉트
                //서버가 직접처리하지않고 presignedUrl을 통해 S3의 주소를 알려줘서 거기서 처리하라고 한다.
                .header(HttpHeaders.LOCATION, presignedUrl)
                .build();
    }

    public S3Client s3Client() {//S3Client: S3 API 호출용 클라이언트(S3에 요청 보내는 객체)

        // 키가 있는경우
        if (accessKey != null && !accessKey.isBlank()) {
            return S3Client.builder()
                    .region(Region.of(region))
                    .credentialsProvider(
                            StaticCredentialsProvider.create(
                                    AwsBasicCredentials.create(
                                            accessKey,
                                            secretKey
                                    )
                            )
                    )
                    .build();
        }
        // 그렇지 않으면: 기본 체인(환경변수, 프로파일, IAM Role)을 자동 탐색
        // 키가 없는경우
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    //S3에 접근 가능한 임시 URL 생성
    private String generatePresignedUrl(String key, String contentType, String fileName) {
        S3Presigner presigner = S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(accessKey != null && !accessKey.isBlank()
                        ? StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey))
                        : DefaultCredentialsProvider.create())
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofSeconds(600))
                .getObjectRequest(req -> req
                        .bucket(bucket)
                        .key(key)
                        .responseContentType(contentType)
                        .responseContentDisposition("attachment; filename=\"" + fileName + "\"")
                        .build())
                .build();

        return presigner.presignGetObject(presignRequest).url().toString();
    }

}
