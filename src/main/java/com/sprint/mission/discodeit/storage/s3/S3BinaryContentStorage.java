package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.InputStream;
import java.time.Duration;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

// discodeit.storage.type 값이 s3인 경우에만 Bean으로 등록
// @ConditionalOnProperty: 특정 환경 속성의 존재 여부나 값에 따라 빈 등록 여부를 결정
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
@Component
public class S3BinaryContentStorage implements BinaryContentStorage {

  // AWS S3 API를 호출하기 위한 동기(+블로킹) 클라이언트
  private final S3Client s3Client;
  // Presigned URL을 생성하기 위한 클라이언트 - S3Presigner는 API를 호출하지 않고 URL만 생성
  private final S3Presigner s3Presigner;
  // S3 버킷 이름
  private final String bucket;
  // Presigned URL의 유효 시간 - application.yaml에서 기본값 5분으로 설정됨
  private final Duration presignedUrlDuration;

  // application.yaml(<- .env)에서 AWS 관련 정보 주입 받음
  // @Value 통해 application.yaml의 프로퍼티 값을 생성자 매개변수에 주입
  public S3BinaryContentStorage(
      @Value("${discodeit.storage.s3.access-key}") String accessKey,
      @Value("${discodeit.storage.s3.secret-key}") String secretKey,
      @Value("${discodeit.storage.s3.region}") String region,
      @Value("${discodeit.storage.s3.bucket}") String bucket,
      @Value("${discodeit.storage.s3.presigned-url-expiration:5m}") Duration presignedUrlDuration
  ) {
    this.bucket = bucket;
    this.presignedUrlDuration = presignedUrlDuration;

    StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(
        AwsBasicCredentials.create(accessKey, secretKey)
    );
    Region awsRegion = Region.of(region);

    this.s3Client = S3Client.builder()
        .region(awsRegion)
        .credentialsProvider(credentialsProvider)
        .build();

    this.s3Presigner = S3Presigner.builder()
        .region(awsRegion)
        .credentialsProvider(credentialsProvider)
        .build();
  }

  @Override
  public UUID put(UUID binaryContentId, byte[] bytes) {
    String key = toKey(binaryContentId);

    // PutObjectRequest: S3에 객체를 업로드하기 위한 요청 객체
    PutObjectRequest putRequest = PutObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .build();
    // TODO: contentType 지정해야 되는지 검토

    // 실제로 S3 API(PutObject)를 호출하여 파일을 업로드
    //  - 내부적으로 HTTP PUT 요청을 S3 엔드포인트로 전송
    //  - 네트워크 오류 시 SdkClientException, 권한 요류 시 S3Exception이 발생
    //    -> 둘다 RuntimeException 이므로 GlobalExceptionHandler에서 처리 TODO: 따로 예외 처리 해야 되는지?
    s3Client.putObject(putRequest, RequestBody.fromBytes(bytes));
    return binaryContentId;
  }

  // S3 버킷에서 파일을 다운로드하여 InputStream으로 반환
  @Override
  public InputStream get(UUID binaryContentId) {
    String key = toKey(binaryContentId);

    // GetObjectRequest: S3에서 객체를 가져오기 위한 요청 객체
    GetObjectRequest getRequest = GetObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .build();

    // s3Client.getObject(): S3 API(GetObject)를 호출하여 파일을 다운로드
    //  - 반환 타입 : ResponseInputStream<GetObjectResponse>
    //  InputStream을 상속받으면서 + GetObjectResponse 객체에 접근할 수 있는 response() 메서드를 제공
    return s3Client.getObject(getRequest);
  }

  // 클라이언트에게 PresignedURL로 리다이렉트 응답(302)을 반환
  //  - 서버는 URL만 생성하고, 클라이언트가 S3에서 직접 다운로드 -> 서버 부하 감소
  @Override
  public ResponseEntity<?> download(BinaryContentDto metaData) {
    String key = toKey(metaData.id());

    // S3 객체에 대한 PresignedURL 생성
    // 대상 S3 객체의 버킷과 키 지정
    GetObjectRequest getRequest = GetObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .build();

    // Presigned URL의 생성 조건을 정의하는 요청 객체 생성
    GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
        .signatureDuration(presignedUrlDuration)
        .getObjectRequest(getRequest)
        .build();

    // Presigned URL 생성
    PresignedGetObjectRequest presigned = s3Presigner.presignGetObject(presignRequest);
    String presignedUrl = presigned.url().toString();

    // 이런 응답 생성
    // HTTP/1.1 302 Found
    // Location: https://bucket.s3.ap-northeast-2.amazonaws.com/uuid?X-Amz-Signature=abc...
    return ResponseEntity
        .status(HttpStatus.FOUND)
        // HttpHeaders.LOCATION — 문자열 상수 "Location". HTTP 표준에서 리다이렉트 대상 URL을 지정하는 헤더
        .header(HttpHeaders.LOCATION, presignedUrl)
        .build();
  }

  // UUID를 S3 객체 키(key)로 변환
  private String toKey(UUID binaryContentId) {
    return binaryContentId.toString();
  }
}
