package com.sprint.mission.discodeit.aws.service;


import com.sprint.mission.discodeit.aws.AwsProperties;
import com.sprint.mission.discodeit.aws.dto.FileResponseDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3UploadService {

  private final S3Client s3Client;
  private final AwsProperties props;
  

  public String store(MultipartFile multipartFile) {
    try {
      String key = makeS3ObjectKey("images", multipartFile);

      PutObjectRequest putReq = PutObjectRequest.builder()
          .bucket(props.getS3().getBucket())
          .key(key)
          .contentType(multipartFile.getContentType())
          .build();

      s3Client.putObject(putReq,
          RequestBody.fromInputStream(multipartFile.getInputStream(), multipartFile.getSize()));
      return buildPublicUrl(props.getS3().getBucket(), props.getRegion(), key);

    } catch (Exception e) {
      throw new RuntimeException("S3 업로드 실패", e);
    }
  }

  private String makeS3ObjectKey(String rootPath, MultipartFile multipartFile) {
    String original = multipartFile.getOriginalFilename();
    String ext = "";
    if (original != null && original.contains(".")) {
      ext = original.substring(original.lastIndexOf('.') + 1);
    }
    LocalDate today = LocalDate.now();
    String datePath = "%04d/%02d".formatted(today.getYear(), today.getMonthValue());
    String filename = UUID.randomUUID() + (ext.isEmpty() ? "" : "." + ext);
    return rootPath + "/" + datePath + "/" + filename;
  }

  private String buildPublicUrl(String bucket, String region, String key) {
    String encodedKey = URLEncoder.encode(key, StandardCharsets.UTF_8).replace("+", "%20");
    if (region == null || region.isBlank() || "us-east-1".equals(region)) {
      return "https://" + bucket + ".s3.amazonaws.com/" + encodedKey;
    }
    return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + encodedKey;
  }

  public List<FileResponseDto> list(String prefix, int maxKeys) {
    String bucket = props.getS3().getBucket();

    ListObjectsV2Request req = ListObjectsV2Request.builder()
        .bucket(bucket)
        .prefix(prefix == null ? "" : prefix)
        .maxKeys(maxKeys <= 0 ? 100 : maxKeys)
        .build();

    ListObjectsV2Response res = s3Client.listObjectsV2(req);

    return res.contents().stream()
        .filter(o -> !o.key().endsWith("/"))
        .map(o -> new FileResponseDto(
            o.key(),
            buildPublicUrl(bucket, props.getRegion(), o.key()),
            o.size(),
            o.lastModified()
        ))
        .toList();
  }

  public String toPublicUrl(String key) {
    return buildPublicUrl(props.getS3().getBucket(), props.getRegion(), key);
  }

  public AwsProperties getProps() {
    return props;
  }
}