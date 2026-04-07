package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.binarycontentdto.BinaryContentDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
public class S3BinaryContentStorage implements BinaryContentStorage {

    private static final Duration PRESIGNED_URL_DURATION = Duration.ofMinutes(5);

    private final String bucket;
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    public S3BinaryContentStorage(
        String bucket,
        S3Client s3Client,
        S3Presigner s3Presigner
    ) {
        this.bucket = bucket;
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
    }

    @Override
    public UUID put(UUID uuid, byte[] bytes) {
        PutObjectRequest request = PutObjectRequest.builder()
            .bucket(bucket)
            .key(uuid.toString())
            .contentType("application/octet-stream")
            .build();

        s3Client.putObject(request, RequestBody.fromBytes(bytes));
        return uuid;
    }

    public UUID put(UUID uuid, MultipartFile file) throws IOException {
        String ext = "";
        if (file.getOriginalFilename() != null) {
            int dotIndex = file.getOriginalFilename().lastIndexOf(".");
            ext = file.getOriginalFilename().substring(dotIndex);
        }

        PutObjectRequest request = PutObjectRequest.builder()
            .bucket(bucket)
            .key(uuid.toString() + ext)
            .contentType("application/octet-stream")
            .build();

        s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));
        return uuid;
    }

    @Override
    public InputStream get(UUID uuid) {
        GetObjectRequest request = GetObjectRequest.builder()
            .bucket(bucket)
            .key(uuid.toString())
            .build();
        return s3Client.getObject(request);
    }

    @Override
    public ResponseEntity<?> download(BinaryContentDto binaryContentDto) {
        return download(binaryContentDto.id().toString(), binaryContentDto);
    }

    public ResponseEntity<?> download(String key, BinaryContentDto binaryContentDto) {
        String url = generatePresignedUrl(
            key,
            resolveFileName(binaryContentDto),
            binaryContentDto.contentType()
        );

        return ResponseEntity.status(302)
            .location(URI.create(url))
            .build();
    }

    String generatePresignedUrl(String key, String fileName, String contentType) {
        GetObjectRequest.Builder requestBuilder = GetObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .responseContentDisposition(buildContentDisposition(fileName));

        if (contentType != null && !contentType.isBlank()) {
            requestBuilder.responseContentType(contentType);
        }

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
            .signatureDuration(PRESIGNED_URL_DURATION)
            .getObjectRequest(requestBuilder.build())
            .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }

    private String resolveFileName(BinaryContentDto binaryContentDto) {
        if (binaryContentDto.fileName() != null && !binaryContentDto.fileName().isBlank()) {
            return binaryContentDto.fileName();
        }
        return binaryContentDto.id().toString();
    }

    private String buildContentDisposition(String fileName) {
        String encoded = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
            .replace("+", "%20");
        return "attachment; filename=\"" + fileName + "\"; filename*=UTF-8''" + encoded;
    }
}
