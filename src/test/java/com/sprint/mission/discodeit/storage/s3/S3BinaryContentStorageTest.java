package com.sprint.mission.discodeit.storage.s3;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.sprint.mission.discodeit.dto.response.BinaryContentDto;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.http.AbortableInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

class S3BinaryContentStorageTest {

	@Test
	void putDelegatesUploadRequestToS3Client() {
		S3Client s3Client = mock(S3Client.class);
		S3Presigner s3Presigner = mock(S3Presigner.class);
		S3BinaryContentStorage storage = new S3BinaryContentStorage(
			"test-bucket",
			Duration.ofMinutes(10),
			s3Client,
			s3Presigner,
			"ap-northeast-2"
		);
		UUID binaryContentId = UUID.randomUUID();
		byte[] bytes = "hello s3".getBytes(StandardCharsets.UTF_8);

		UUID savedId = storage.put(binaryContentId, bytes);

		ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
		verify(s3Client).putObject(requestCaptor.capture(), any(software.amazon.awssdk.core.sync.RequestBody.class));
		PutObjectRequest request = requestCaptor.getValue();

		assertEquals(binaryContentId, savedId);
		assertEquals("test-bucket", request.bucket());
		assertEquals(binaryContentId.toString(), request.key());
	}

	@Test
	void getReturnsInputStreamFromS3() throws IOException {
		S3Client s3Client = mock(S3Client.class);
		S3Presigner s3Presigner = mock(S3Presigner.class);
		S3BinaryContentStorage storage = new S3BinaryContentStorage(
			"test-bucket",
			Duration.ofMinutes(10),
			s3Client,
			s3Presigner,
			"ap-northeast-2"
		);
		UUID binaryContentId = UUID.randomUUID();
		byte[] expected = "download me".getBytes(StandardCharsets.UTF_8);

		ResponseInputStream<GetObjectResponse> responseInputStream = new ResponseInputStream<>(
			GetObjectResponse.builder()
				.contentLength((long)expected.length)
				.lastModified(Instant.now())
				.build(),
			AbortableInputStream.create(new ByteArrayInputStream(expected))
		);
		when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(responseInputStream);

		byte[] actual;
		try (InputStream inputStream = storage.get(binaryContentId)) {
			actual = inputStream.readAllBytes();
		}

		ArgumentCaptor<GetObjectRequest> requestCaptor = ArgumentCaptor.forClass(GetObjectRequest.class);
		verify(s3Client).getObject(requestCaptor.capture());
		GetObjectRequest request = requestCaptor.getValue();

		assertEquals("test-bucket", request.bucket());
		assertEquals(binaryContentId.toString(), request.key());
		assertArrayEquals(expected, actual);
	}

	@Test
	void downloadRedirectsToPresignedUrl() {
		String expectedUrl = "https://example.com/presigned/download";
		TestableS3BinaryContentStorage storage = new TestableS3BinaryContentStorage(expectedUrl);
		BinaryContentDto metaData = new BinaryContentDto(
			UUID.randomUUID(),
			"report.pdf",
			128L,
			"application/pdf"
		);

		var response = storage.download(metaData);

		assertEquals(302, response.getStatusCode().value());
		assertEquals(URI.create(expectedUrl), response.getHeaders().getLocation());
		assertEquals("no-store", response.getHeaders().getCacheControl());
		assertEquals(metaData.id(), storage.lastBinaryContentId);
		assertTrue(storage.lastFileName.equals("report.pdf"));
		assertTrue(storage.lastContentType.equals("application/pdf"));
	}

	private static class TestableS3BinaryContentStorage extends S3BinaryContentStorage {

		private final String expectedUrl;
		private UUID lastBinaryContentId;
		private String lastFileName;
		private String lastContentType;

		private TestableS3BinaryContentStorage(String expectedUrl) {
			super(
				"test-bucket",
				Duration.ofMinutes(10),
				mock(S3Client.class),
				mock(S3Presigner.class),
				"ap-northeast-2"
			);
			this.expectedUrl = expectedUrl;
		}

		@Override
		protected String generatePresignedUrl(String key, String fileName, String contentType) {
			this.lastBinaryContentId = UUID.fromString(key);
			this.lastFileName = fileName;
			this.lastContentType = contentType;
			return expectedUrl;
		}
	}
}
