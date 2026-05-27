package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.config.AwsProperties;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.UUID;

@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
public class S3BinaryContentStorage implements BinaryContentStorage {
    private final S3Client s3Client;
    private final AwsProperties props;
    private final S3Presigner s3Presigner;

    public S3BinaryContentStorage(S3Client s3Client, AwsProperties props, S3Presigner s3Presigner) {
        this.s3Client = s3Client;
        this.props = props;
        this.s3Presigner = s3Presigner;
    }

    private S3Client getS3Client(){
        return s3Client;
    }

    @Override
    public UUID put(UUID binaryContentId, byte[] bytes, String fileName, String contentType) {
        try{
            String key = generateKey(binaryContentId, fileName);

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(props.getBucket())
                    .key(key)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(bytes));

            return binaryContentId;
        }catch (Exception e){
            throw new RuntimeException("S3 업로드 중 오류 발생: " + binaryContentId, e);
        }
    }

    @Override
    public InputStream get(UUID binaryContentId, String fileName) {
        try{
            String key = generateKey(binaryContentId, fileName);

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(props.getBucket())
                    .key(key)
                    .build();

            return s3Client.getObject(getObjectRequest);

        }catch (Exception e){
            throw new RuntimeException("S3 데이터 조회 중 오류 발생", e);
        }
    }

    @Override
    public ResponseEntity<?> download(BinaryContentDto metaData) {
        String presignedUrl = generatePresignedUrl(metaData.id() + "." + getExtension(metaData.fileName()));

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(presignedUrl))
                .build();
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    private String generatePresignedUrl(String key){
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(props.getBucket())
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofSeconds(props.getPresignedUrlExpiration()))
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);

        return presignedRequest.url().toString();
    }

    private String generateKey(UUID binaryContentId, String fileName){
        return binaryContentId + "." + getExtension(fileName);
    }
}
