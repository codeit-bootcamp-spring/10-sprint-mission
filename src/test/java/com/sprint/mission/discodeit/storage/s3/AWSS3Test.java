package com.sprint.mission.discodeit.storage.s3;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@ExtendWith(MockitoExtension.class)
class AWSS3Test {

  @Mock
  private S3Client s3Client;

  @Mock
  private S3Presigner s3Presigner;

  private String bucket = "test-bucket";

  @Test
  @DisplayName("S3 버킷에 파일 업로드를 성공해야 한다.")
  void should_upload_in_s3_bucket() {
    // given
    String key = "test/" + UUID.randomUUID();
    byte[] fakeImageBytes = "가짜 이미지 데이터".getBytes();
    given(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
        .willReturn(PutObjectResponse.builder().build());
    // when
    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .build();
    s3Client.putObject(putObjectRequest, RequestBody.fromBytes(fakeImageBytes));

    // then
    then(s3Client).should(times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
  }

  @Test
  @DisplayName("Presigned URL 생성에 성공해야 한다.")
  void should_download_in_s3_bucket_when_use_presigned_url() throws MalformedURLException {
    // given
    String key = "test/" + UUID.randomUUID();
    String expectedUrl = "https://test-bucket.s3.amazonaws.com/test/file.png?token=fake";

    PresignedGetObjectRequest mockPresignedRequest = mock(PresignedGetObjectRequest.class);
    given(mockPresignedRequest.url()).willReturn(new URL(expectedUrl));
    given(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
        .willReturn(mockPresignedRequest);

    // when
    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .build();

    GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
        .signatureDuration(Duration.ofMinutes(10))
        .getObjectRequest(getObjectRequest)
        .build();

    String url = s3Presigner.presignGetObject(presignRequest).url().toString();

    // then
    assertNotNull(url);
    assertEquals(expectedUrl, url);
    then(s3Presigner).should(times(1)).presignGetObject(any(GetObjectPresignRequest.class));
  }
}