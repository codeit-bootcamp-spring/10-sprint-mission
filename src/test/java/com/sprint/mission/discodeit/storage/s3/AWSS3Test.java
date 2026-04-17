package com.sprint.mission.discodeit.storage.s3;

import java.io.FileInputStream;
import java.time.Duration;
import java.util.Properties;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

public class AWSS3Test {
  // S3 연결 확인을 위한 클래스(통합 테스트로 확인)빈 등록X

  // 환경변수 가져오기(env 직접)
  public Properties loadEnv() throws Exception {
    Properties props = new Properties();
    try (FileInputStream fis = new FileInputStream(".env")) {
      props.load(fis);
    }
    return props;
  }

  // 업로드 테스트
  public void upload(S3Client s3Client, String bucket, String key, byte[] data) {
    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .build();

    s3Client.putObject(putObjectRequest, RequestBody.fromBytes(data));
  }

  // 다운로드 테스트
  public void download(S3Client s3Client, String bucket, String key) {
    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .build();

    ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(getObjectRequest);
  }

  // PresignedUrl 생성
  public String generatePresignedUrl(S3Presigner presigner, String bucket, String key) {
    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .build();

    GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
        .signatureDuration(Duration.ofMinutes(10)) // 10분 유효
        .getObjectRequest(getObjectRequest)
        .build();

    return presigner.presignGetObject(presignRequest).url().toString();
  }
}
