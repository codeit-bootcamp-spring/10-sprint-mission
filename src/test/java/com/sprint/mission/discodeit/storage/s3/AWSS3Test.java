package com.sprint.mission.discodeit.storage.s3;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Properties;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

public class AWSS3Test {

	private static final Duration PRESIGNED_URL_EXPIRATION = Duration.ofMinutes(10);

	private S3Client s3Client;
	private S3Presigner s3Presigner;
	private AwsS3Properties awsS3Properties;
	private String testKey;

	@BeforeEach
	void setUp() throws IOException {
		Path envPath = Path.of(".env");
		Assumptions.assumeTrue(Files.exists(envPath), ".env 파일이 없어서 실제 AWS S3 테스트를 건너뜁니다.");

		this.awsS3Properties = AwsS3Properties.loadFromEnvFile(envPath);
		Assumptions.assumeTrue(awsS3Properties.isComplete(), "AWS S3 설정이 완전하지 않아 테스트를 건너뜁니다.");

		AwsBasicCredentials credentials = AwsBasicCredentials.create(
			awsS3Properties.accessKey(),
			awsS3Properties.secretKey()
		);

		Region region = Region.of(awsS3Properties.region());

		this.s3Client = S3Client.builder()
			.region(region)
			.credentialsProvider(StaticCredentialsProvider.create(credentials))
			.build();

		this.s3Presigner = S3Presigner.builder()
			.region(region)
			.credentialsProvider(StaticCredentialsProvider.create(credentials))
			.build();

		this.testKey = "awss3test/" + UUID.randomUUID() + ".txt";
	}

	@AfterEach
	void tearDown() {
		if (s3Client != null && awsS3Properties != null && objectExists(testKey)) {
			s3Client.deleteObject(DeleteObjectRequest.builder()
				.bucket(awsS3Properties.bucket())
				.key(testKey)
				.build());
		}

		if (s3Presigner != null) {
			s3Presigner.close();
		}

		if (s3Client != null) {
			s3Client.close();
		}
	}

	@Test
	void uploadTest() {
		byte[] content = "S3 upload test".getBytes(StandardCharsets.UTF_8);

		PutObjectRequest request = PutObjectRequest.builder()
			.bucket(awsS3Properties.bucket())
			.key(testKey)
			.contentType("text/plain")
			.build();

		s3Client.putObject(request, RequestBody.fromBytes(content));

		assertTrue(objectExists(testKey));
	}

	@Test
	void downloadTest() {
		byte[] uploadContent = "S3 download test".getBytes(StandardCharsets.UTF_8);

		s3Client.putObject(
			PutObjectRequest.builder()
				.bucket(awsS3Properties.bucket())
				.key(testKey)
				.contentType("text/plain")
				.build(),
			RequestBody.fromBytes(uploadContent)
		);

		ResponseBytes<GetObjectResponse> response = s3Client.getObjectAsBytes(
			GetObjectRequest.builder()
				.bucket(awsS3Properties.bucket())
				.key(testKey)
				.build()
		);

		String downloaded = response.asString(StandardCharsets.UTF_8);

		assertEquals("S3 download test", downloaded);
		assertEquals("text/plain", response.response().contentType());
	}

	@Test
	void generatePresignedUrlTest() {
		byte[] uploadContent = "S3 presigned url test".getBytes(StandardCharsets.UTF_8);

		s3Client.putObject(
			PutObjectRequest.builder()
				.bucket(awsS3Properties.bucket())
				.key(testKey)
				.contentType("text/plain")
				.build(),
			RequestBody.fromBytes(uploadContent)
		);

		String presignedUrl = s3Presigner.presignGetObject(
			GetObjectPresignRequest.builder()
				.signatureDuration(PRESIGNED_URL_EXPIRATION)
				.getObjectRequest(GetObjectRequest.builder()
					.bucket(awsS3Properties.bucket())
					.key(testKey)
					.build())
				.build()
		).url().toString();

		assertNotNull(presignedUrl);
		assertTrue(presignedUrl.contains(awsS3Properties.bucket()));
		assertTrue(presignedUrl.contains(testKey));
	}

	private boolean objectExists(String key) {
		if (key == null || key.isBlank()) {
			return false;
		}

		try {
			s3Client.headObject(HeadObjectRequest.builder()
				.bucket(awsS3Properties.bucket())
				.key(key)
				.build());
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private record AwsS3Properties(
		String accessKey,
		String secretKey,
		String region,
		String bucket
	) {

		static AwsS3Properties loadFromEnvFile(Path envPath) throws IOException {
			if (!Files.exists(envPath)) {
				throw new IllegalStateException(".env 파일을 찾을 수 없습니다: " + envPath.toAbsolutePath());
			}

			Properties properties = new Properties();

			try (Reader reader = Files.newBufferedReader(envPath, StandardCharsets.UTF_8);
				 BufferedReader bufferedReader = new BufferedReader(reader)) {

				String line;
				while ((line = bufferedReader.readLine()) != null) {
					line = line.trim();

					if (line.isEmpty() || line.startsWith("#")) {
						continue;
					}

					int separatorIndex = line.indexOf('=');
					if (separatorIndex < 0) {
						continue;
					}

					String key = line.substring(0, separatorIndex).trim();
					String value = line.substring(separatorIndex + 1).trim();

					if ((value.startsWith("\"") && value.endsWith("\""))
						|| (value.startsWith("'") && value.endsWith("'"))) {
						value = value.substring(1, value.length() - 1);
					}

					properties.setProperty(key, value);
				}
			}

			return new AwsS3Properties(
				properties.getProperty("AWS_S3_ACCESS_KEY"),
				properties.getProperty("AWS_S3_SECRET_KEY"),
				properties.getProperty("AWS_S3_REGION"),
				properties.getProperty("AWS_S3_BUCKET")
			);
		}

		boolean isComplete() {
			return isNotBlank(accessKey)
				&& isNotBlank(secretKey)
				&& isNotBlank(region)
				&& isNotBlank(bucket);
		}

		private static boolean isNotBlank(String value) {
			return value != null && !value.isBlank();
		}
	}
}
