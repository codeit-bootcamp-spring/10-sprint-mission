package com.sprint.mission.discodeit.storage.s3.service;

import com.sprint.mission.discodeit.storage.s3.config.Properties;
import com.sprint.mission.discodeit.storage.s3.dto.FileResponseDto;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class S3UploadService {

  private final S3Client s3Client;
  private final Properties props;

  /**
   * 파일을 S3에 업로드하고 퍼블릭 URL을 반환한다.
   */
  public String store(MultipartFile multipartFile) {
    try {
      // 1) 키 생성 규칙: images/YYYY/MM/UUID.원본확장자
      String key = makeS3ObjectKey("images", multipartFile);

      // 2) PutObjectRequest 생성 (버킷 정책이 퍼블릭 읽기면 acl 생략해도 됨)
      PutObjectRequest putReq = PutObjectRequest.builder()
          .bucket(props.getS3().getBucket())
          .key(key)
          .contentType(multipartFile.getContentType())
          // .acl(ObjectCannedACL.PUBLIC_READ) // 필요 시 주석 해제
          .build();

      // 3) 업로드 (임시파일 없이 InputStream으로)
      s3Client.putObject(putReq,
          RequestBody.fromInputStream(multipartFile.getInputStream(), multipartFile.getSize()));

      // 4) 퍼블릭 URL 생성 후 반환
      return buildPublicUrl(props.getS3().getBucket(), props.getRegion(), key);

    } catch (Exception e) {
      throw new RuntimeException("S3 업로드 실패", e);
    }
    // ⚠ 주입받은 s3Client는 닫지 말 것 (Bean 공용)
  }

  private String makeS3ObjectKey(String rootPath, MultipartFile multipartFile) {
    String original = multipartFile.getOriginalFilename();
    String ext = "";
    if (original != null && original.contains(".")) {
      ext = original.substring(original.lastIndexOf('.') + 1);
    }
    LocalDate today = LocalDate.now();
    String datePath = "%04d/%02d".formatted(today.getYear(), today.getMonthValue());
    String filename = UUID.randomUUID().toString() + (ext.isEmpty() ? "" : "." + ext);
    return rootPath + "/" + datePath + "/" + filename;
  }

  private String buildPublicUrl(String bucket, String region, String key) {
    String encodedKey = URLEncoder.encode(key, StandardCharsets.UTF_8).replace("+", "%20");
    if (region == null || region.isBlank() || "us-east-1".equals(region)) {
      return "https://" + bucket + ".s3.amazonaws.com/" + encodedKey;
    }
    return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + encodedKey;
  }

  /**
   * 리스트 API: prefix로 필터링해 파일 목록을 반환
   */
  public List<FileResponseDto> list(String prefix, int maxKeys) {
    String bucket = props.getS3().getBucket();

    ListObjectsV2Request req = ListObjectsV2Request.builder()
        .bucket(bucket)
        .prefix(prefix == null ? "" : prefix)
        .maxKeys(maxKeys <= 0 ? 100 : maxKeys)
        .build();

    ListObjectsV2Response res = s3Client.listObjectsV2(req);

    return res.contents().stream()
        .filter(o -> !o.key().endsWith("/")) // 폴더 유사 key 제외
        .map(o -> new FileResponseDto(
            o.key(),
            buildPublicUrl(bucket, props.getRegion(), o.key()),
            o.size(),
            o.lastModified()
        ))
        .toList();
  }

  /**
   * 컨트롤러 등에서 key → 퍼블릭 URL 변환이 필요할 때 호출
   */
  public String toPublicUrl(String key) {
    return buildPublicUrl(props.getS3().getBucket(), props.getRegion(), key);
  }

  /**
   * 버킷/리전 등 설정 접근이 필요할 때 사용
   */
  public Properties getProps() {
    return props;
  }
}