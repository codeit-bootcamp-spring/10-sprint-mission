package com.sprint.mission.discodeit.storage.s3;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;
import java.time.Duration;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentReadException;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentSaveException;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentStorageInitException;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Slf4j
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
@Component
public class S3BinaryContentStorage implements BinaryContentStorage {

	private final String bucket;
	private final Duration presignedUrlExpiration;
	private final S3Client s3Client;
	private final S3Presigner s3Presigner;
	private final String region;

	@Autowired
	public S3BinaryContentStorage(
		@Value("${discodeit.storage.s3.access-key}") String accessKey,
		@Value("${discodeit.storage.s3.secret-key}") String secretKey,
		@Value("${discodeit.storage.s3.region}") String region,
		@Value("${discodeit.storage.s3.bucket}") String bucket,
		@Value("${discodeit.storage.s3.presigned-url-expiration:600}") long presignedUrlExpirationSeconds
	) {
		this(
			bucket,
			Duration.ofSeconds(presignedUrlExpirationSeconds),
			createS3Client(accessKey, secretKey, region),
			createS3Presigner(accessKey, secretKey, region),
			region
		);
	}

	S3BinaryContentStorage(
		String bucket,
		Duration presignedUrlExpiration,
		S3Client s3Client,
		S3Presigner s3Presigner,
		String region
	) {
		this.bucket = bucket;
		this.presignedUrlExpiration = presignedUrlExpiration;
		this.s3Client = s3Client;
		this.s3Presigner = s3Presigner;
		this.region = region;
	}

	@PostConstruct
	public void init() {
		try {
			s3Client.headBucket(HeadBucketRequest.builder()
				.bucket(bucket)
				.build());
			log.info("[BINARY_CONTENT_STORAGE_INIT] S3 저장소 준비 완료: bucket={}, region={}", bucket,
				region);
		} catch (Exception e) {
			log.error("[BINARY_CONTENT_STORAGE_INIT] S3 저장소 초기화 실패: bucket={}, region={}", bucket,
				region, e);
			throw new BinaryContentStorageInitException(Path.of("s3://" + bucket), e);
		}
	}

	@PreDestroy
	public void destroy() {
		s3Presigner.close();
		s3Client.close();
	}

	@Override
	public UUID put(UUID binaryContentId, byte[] bytes) {
		String key = binaryContentId.toString();

		try {
			s3Client.putObject(PutObjectRequest.builder()
					.bucket(bucket)
					.key(key)
					.build(),
				RequestBody.fromBytes(bytes));
			return binaryContentId;
		} catch (Exception e) {
			log.error("[BINARY_CONTENT_PUT] S3 파일 저장 실패: binaryContentId={}, bucket={}",
				binaryContentId, bucket, e);
			throw new BinaryContentSaveException(binaryContentId, e);
		}
	}

	@Override
	public InputStream get(UUID binaryContentId) {
		String key = binaryContentId.toString();

		try {
			return s3Client.getObject(GetObjectRequest.builder()
				.bucket(bucket)
				.key(key)
				.build());
		} catch (Exception e) {
			log.error("[BINARY_CONTENT_GET] S3 파일 조회 실패: binaryContentId={}, bucket={}",
				binaryContentId, bucket, e);
			throw new BinaryContentReadException(binaryContentId, e);
		}
	}

	@Override
	public ResponseEntity<Void> download(BinaryContentDto metaData) {
		String key = metaData.id().toString();
		String presignedUrl = generatePresignedUrl(key, metaData.fileName(), metaData.contentType());

		log.debug("[BINARY_CONTENT_DOWNLOAD] Presigned URL 리다이렉트: binaryContentId={}, bucket={}",
			metaData.id(), bucket);

		return ResponseEntity.status(HttpStatus.FOUND)
			.location(URI.create(presignedUrl))
			.header(HttpHeaders.CACHE_CONTROL, "no-store")
			.build();
	}

	protected String generatePresignedUrl(String key, String fileName, String contentType) {
		try {
			GetObjectRequest getObjectRequest = GetObjectRequest.builder()
				.bucket(bucket)
				.key(key)
				.responseContentType(contentType)
				.responseContentDisposition("attachment; filename=\"" + fileName + "\"")
				.build();

			GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
				.signatureDuration(presignedUrlExpiration)
				.getObjectRequest(getObjectRequest)
				.build();

			return s3Presigner.presignGetObject(presignRequest).url().toString();
		} catch (Exception e) {
			log.error("[BINARY_CONTENT_DOWNLOAD] Presigned URL 생성 실패: key={}, bucket={}", key,
				bucket, e);
			throw new BinaryContentReadException(fileName, e);
		}
	}

	private static S3Client createS3Client(String accessKey, String secretKey, String region) {
		StaticCredentialsProvider credentialsProvider = createCredentialsProvider(accessKey, secretKey);
		return S3Client.builder()
			.region(Region.of(region))
			.credentialsProvider(credentialsProvider)
			.build();
	}

	private static S3Presigner createS3Presigner(String accessKey, String secretKey, String region) {
		StaticCredentialsProvider credentialsProvider = createCredentialsProvider(accessKey, secretKey);
		return S3Presigner.builder()
			.region(Region.of(region))
			.credentialsProvider(credentialsProvider)
			.build();
	}

	private static StaticCredentialsProvider createCredentialsProvider(String accessKey,
		String secretKey) {
		AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
		return StaticCredentialsProvider.create(credentials);
	}
}
