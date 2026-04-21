package com.sprint.mission.discodeit.storage.s3;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.Properties;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

/*
Spring 컨텍스트 없이 순수 AWS SDK(S3Client, S3Presigner)만 사용하여
S3의 기본 동작(업로드/다운로드/Presigned URL 생성을 검증

프로젝트 루트의 .env 파일에서 AWS 자격증명을 java.util.Properties로 직접 로드

upload -> download -> generatePresignedUrl 순서로 실행
 */

// TODO: 로깅 추가 고려
@EnabledIfEnvironmentVariable(named = "AWS_ACCESS_KEY_ID", matches = ".+")
public class AWSS3Test {

  // S3 업로드/다운로드용 클라이언트
  static S3Client s3Client;
  // Presigned URL 생성 전용 클라이언트
  //  - AWS S3 리소스에 대해 임시적인 접근 권한을 부여하는 URL
  static S3Presigner s3Presigner;
  // S3 버킷 이름
  static String bucketName;
  // 테스트마다 고유한 S3 객체 키 (충돌 방지)
  String testKey;

  // .env에서 AWS 자격증명으로 로드하고 클라이언트를 초기화
  // @BeforeAll 시점에는 테스트 인스턴스 생성되지 않았으므로 관련된 것 static으로 선언
  @BeforeAll
  static void setUp() throws IOException {
    // java.util.Properties를 사용하여 .env 파일을 "키=값" 형식으로 파싱
    Properties properties = new Properties();
    try (FileInputStream fis = new FileInputStream(".env")) {
      // 스트림으로부터 속성 목록을 읽어옴
      properties.load(fis);
    }

    // getProperty(String key): 지정한 키에 해당하는 값을 가져옴
    String accessKey = properties.getProperty("AWS_S3_ACCESS_KEY");     // IAM 액세스 키
    String secretKey = properties.getProperty("AWS_S3_SECRET_KEY"); // IAM 시크릿 키
    String region = properties.getProperty("AWS_S3_REGION");               // AWS 리전
    bucketName = properties.getProperty("AWS_S3_BUCKET");               // S3 버킷 이름

    // 하나라도 누락될 경우 바로 실패 (fail fast)
    if (accessKey == null || secretKey == null || region == null || bucketName == null) {
      throw new IllegalStateException("AWS S3 설정이 .env 파일에 올바르게 정의되지 않았습니다.");
    }

    // StaticCredentialsProvider: 코드 내에 직접 정의한 고정된 자격 증명(Access Key, Secret Key)을
    // 사용하여 AWS 서비스에 인증할 때 사용
    // 보안상 실제 운영 환경에서는 액세스 키를 코드에 직접 하드코딩하는 대신
    // DefaultCredentialsProvider와 같은 자동 탐색 체인을 사용하는 것이 권장됨
    // StaticCredentialsProvider 생성
    StaticCredentialsProvider credentials = StaticCredentialsProvider.create(
        // 자격 증명 객체 생성
        AwsBasicCredentials.create(accessKey, secretKey)
    );
    // 문자열 -> Region 객체로 변환
    Region awsRegion = Region.of(region);

    s3Client = S3Client.builder()
        .region(awsRegion)
        .credentialsProvider(credentials)
        .build();

    s3Presigner = S3Presigner.builder()
        .region(awsRegion)
        .credentialsProvider(credentials)
        .build();
  }

  // 각 테스트 메서드 실행 전 호출 - 고유한 테스트 키를 생성
  // 매 테스트마다 UUID 기반의 고유키 사용하여 테스트 간 간섭 방지
  @BeforeEach
  void initTestKey() {
    testKey = "test/" + UUID.randomUUID();
  }

  // 콘솔 및 브라우저에서 확인 시 주석 처리할 것
  @AfterEach
  void cleanup() {
    try {
      s3Client.deleteObject(
          DeleteObjectRequest.builder()
              .bucket(bucketName)
              .key(testKey)
              .build()
      );
    } catch (S3Exception e) {
      // 정리 실패는 무시 (best-effort)
    }
  }

  // 업로드
  @Test
  void upload() {
    // given
    String content = "test-upload-content";

    // when
    // pubObject: S3에 파일 업로드 실행
    PutObjectResponse response = s3Client.putObject(
        PutObjectRequest.builder()
            .bucket(bucketName)
            .key(testKey)
            .contentType("text/plain")
            .build(),
        RequestBody.fromString(content) // String 데이터를 S3 객체의 내용(Body)으로 변환
    );

    // then
    // HTTP 응답이 2xx success 인지 검증
    // response.sdkHttpResponse() : AWS SDK v2에서 API 호출 결과 객체로부터 실제 HTTP 응답 정보를 가져옴
    // .isSuccessful() : HTTP 상태 코드가 200 OK 계열(200~299)인지 확인하여 boolean 값을 반환
    assertThat(response.sdkHttpResponse().isSuccessful()).isTrue();
  }

  // 다운로드
  @Test
  void download() throws IOException {
    // given
    String content = "test-download-content";
    s3Client.putObject(
        PutObjectRequest.builder()
            .bucket(bucketName)
            .key(testKey)
            .contentType("text/plain")
            .build(),
        RequestBody.fromString(content)
    );

    // when
    // ResponseInputStream : 파일 데이터(Stream) + 메타데이터(Response)
    try (
        ResponseInputStream<GetObjectResponse> response = s3Client.getObject(
            GetObjectRequest.builder()
                .bucket(bucketName)
                .key(testKey)
                .build());
    ) {
      // then
      assertThat(response.response().contentType()).isEqualTo("text/plain");

      // readAllBytes() : 스트림 전체를 바이트 배열로 가져옴
      byte[] bytes = response.readAllBytes();
      assertThat(new String(bytes)).isEqualTo(content);
    }
  }

  // PresignedUrl 생성
  // GeneratePresignedGetUrlAndRetrieve.java 공식 문서
  // https://github.com/awsdocs/aws-doc-sdk-examples/blob/d73001daea05266eaa9e074ccb71b9383832369a/javav2/example_code/s3/src/main/java/com/example/s3/GeneratePresignedGetUrlAndRetrieve.java
  //   예시: https://bucket-name.s3.ap-northeast-2.amazonaws.com/object-key
  //         ?X-Amz-Algorithm=AWS4-HMAC-SHA256
  //         &X-Amz-Credential=AKIA.../20260407/ap-northeast-2/s3/aws4_request
  //         &X-Amz-Date=20260407T120000Z
  //         &X-Amz-Expires=600
  //         &X-Amz-SignedHeaders=host
  //         &X-Amz-Signature=abc123...
  //  - URL 자체에 서명 정보가 쿼리 파라미터로 포함되어 있다
  //  - 내부적 원리는 -> Digital Signature
  //  - URL이 만료되거나, 서명이 유효하지 않으면 403 Forbidden이 반환됨
  @Test
  void generatePresignedUrl() {
    // given
    String content = "test-presignedURL-content";
    s3Client.putObject(
        PutObjectRequest.builder()
            .bucket(bucketName)
            .key(testKey)
            .contentType("text/plain")
            .build(),
        RequestBody.fromString(content)
    );

    // when
    // 대상 S3 객체의 버킷과 키 지정
    GetObjectRequest objectRequest = GetObjectRequest.builder()
        .bucket(bucketName)
        .key(testKey)
        .build();

    // Presigned URL의 생성 조건을 정의하는 요청 객체 생성
    GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
        .signatureDuration(Duration.ofMinutes(10)) // URL의 유효 기간 (10분 후 만료)
        .getObjectRequest(objectRequest) // 객체 위치 정보 연결
        .build();

    // Presigned URL 생성
    PresignedGetObjectRequest presigned = s3Presigner.presignGetObject(presignRequest);

    // then
    URL url = presigned.url();
    assertThat(url).isNotNull();
    // 생성된 URL을 콘솔에 출력하여 확인 (브라우저에서 직접 테스트)
    System.out.println("Presigned URL: " + url);
  }

}
