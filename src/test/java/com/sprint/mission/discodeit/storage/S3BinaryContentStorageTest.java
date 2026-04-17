package com.sprint.mission.discodeit.storage;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import com.sprint.mission.discodeit.config.S3Properties;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.exception.binarycontent.StorageDownloadFailedException;
import com.sprint.mission.discodeit.exception.binarycontent.StorageGetFailedException;
import com.sprint.mission.discodeit.exception.binarycontent.StorageUploadFailedException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@ExtendWith(MockitoExtension.class)
class S3BinaryContentStorageTest {

  @Mock
  private S3Presigner s3Presigner;
  @Mock
  private S3Client s3Client;
  @Mock
  private S3Properties s3Properties;

  @InjectMocks
  private S3BinaryContentStorage s3BinaryContentStorage;

  @Test
  @DisplayName("성공: S3에 파일 업로드 성공")
  void putFileToS3Success() {
    //given
    UUID fileId = UUID.randomUUID();
    String key = "uploads/" + fileId.toString();
    given(s3Properties.getBucket()).willReturn("bucketName");

    //when
    s3BinaryContentStorage.put(fileId, new byte[]{0, 12, 3});

    //then
    ArgumentCaptor<PutObjectRequest> putObjectCaptor = ArgumentCaptor.forClass(
        PutObjectRequest.class);
    then(s3Client).should().putObject(putObjectCaptor.capture(), any(RequestBody.class));
    PutObjectRequest capturedRequest = putObjectCaptor.getValue();
    assertEquals("bucketName", capturedRequest.bucket());
    assertEquals(key, capturedRequest.key());

  }

  @Test
  @DisplayName("실패: S3에 파일 업로드 중 오류 발생")
  void putFileToS3Failure() {
    //given
    UUID fileId = UUID.randomUUID();
    AwsErrorDetails errorDetails = AwsErrorDetails.builder()
        .errorCode("S3_UPLOAD_ERROR")
        .errorMessage("S3 서비스 응답 오류")
        .build();
    given(s3Properties.getBucket()).willReturn("bucketName");
    given(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class))).willThrow(
        S3Exception.builder().awsErrorDetails(errorDetails).build());

    //when&then
    assertThrows(
        StorageUploadFailedException.class,
        () -> s3BinaryContentStorage.put(fileId, new byte[]{0, 12, 3}));

  }

  @Test
  @DisplayName("성공: S3에서 바이트 코드 가져오기 성공")
  void getFileFromS3Success() throws IOException {
    //given
    UUID fileId = UUID.randomUUID();
    String key = "uploads/" + fileId.toString();
    byte[] expectedBytes = new byte[]{1, 2, 3};
    ResponseBytes<GetObjectResponse> objectBytes = mock(ResponseBytes.class);
    given(s3Properties.getBucket()).willReturn("bucketName");
    given(s3Client.getObjectAsBytes(any(GetObjectRequest.class))).willReturn(objectBytes);
    given(objectBytes.asInputStream()).willReturn(new ByteArrayInputStream(expectedBytes));

    //when
    InputStream result = s3BinaryContentStorage.get(fileId);

    //then
    ArgumentCaptor<GetObjectRequest> getObjectCaptor = ArgumentCaptor.forClass(
        GetObjectRequest.class);
    then(s3Client).should().getObjectAsBytes(getObjectCaptor.capture());
    GetObjectRequest capturedRequest = getObjectCaptor.getValue();
    assertEquals("bucketName", capturedRequest.bucket());
    assertEquals(key, capturedRequest.key());
    assertArrayEquals(expectedBytes, result.readAllBytes());

  }

  @Test
  @DisplayName("실패: S3에서 바이트 코드 가져오는 중 오류 발생")
  void getFileToS3Failure() {
    //given
    UUID fileId = UUID.randomUUID();
    AwsErrorDetails errorDetails = AwsErrorDetails.builder()
        .errorCode("S3_GET_ERROR")
        .errorMessage("S3 서비스 응답 오류")
        .build();
    given(s3Properties.getBucket()).willReturn("bucketName");
    given(s3Client.getObjectAsBytes(any(GetObjectRequest.class))).willThrow(
        S3Exception.builder().awsErrorDetails(errorDetails).build());

    //when&then
    assertThrows(
        StorageGetFailedException.class,
        () -> s3BinaryContentStorage.get(fileId));

  }

  @Test
  @DisplayName("성공: S3에서 파일 URL 받아서 리다이렉션 반환 성공")
  void downloadFileFromS3Success() throws Exception {
    //given
    BinaryContentDto dto = new BinaryContentDto(UUID.randomUUID(), "fileName", 50L, "jpg");
    URL fakeUrl = new URI("https://fake-s3-bucket.s3.amazonaws.com/test").toURL();
    PresignedGetObjectRequest presignedRequest = mock(PresignedGetObjectRequest.class);

    given(s3Properties.getBucket()).willReturn("bucketName");
    given(s3Properties.getPresignedUrlExpiration()).willReturn(600);
    given(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class))).willReturn(
        presignedRequest);
    given(presignedRequest.url()).willReturn(fakeUrl);

    //when
    ResponseEntity<?> result = s3BinaryContentStorage.download(dto);

    //then
    assertEquals(HttpStatus.FOUND, result.getStatusCode());
    assertEquals(fakeUrl.toString(), result.getHeaders().getLocation().toString());
  }

  @Test
  @DisplayName("실패: S3에서 파일 URL 받아서 리다이렉션 반환 중 오류 발생")
  void downloadFileToS3Failure() {
    //given
    BinaryContentDto dto = new BinaryContentDto(UUID.randomUUID(), "fileName", 50L, "jpg");
    PresignedGetObjectRequest presignedRequest = mock(PresignedGetObjectRequest.class);

    AwsErrorDetails errorDetails = AwsErrorDetails.builder()
        .errorCode("S3_GET_ERROR")
        .errorMessage("S3 서비스 응답 오류")
        .build();

    given(s3Properties.getBucket()).willReturn("bucketName");
    given(s3Properties.getPresignedUrlExpiration()).willReturn(600);
    given(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class))).willThrow(
        S3Exception.builder().awsErrorDetails(errorDetails).build());

    //when&then
    assertThrows(
        StorageDownloadFailedException.class,
        () -> s3BinaryContentStorage.download(dto));

  }
}