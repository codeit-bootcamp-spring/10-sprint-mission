package com.sprint.mission.discodeit.aws.controller;

import com.sprint.mission.discodeit.aws.AwsProperties;
import com.sprint.mission.discodeit.aws.dto.FileResponseDto;
import com.sprint.mission.discodeit.aws.service.S3UploadService;
import java.net.URI;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FilesApiController {

  private final S3UploadService s3;
  private final S3Presigner presigner;   // ✅ 프리사이너 주입
  private final AwsProperties props;     // 버킷/리전 접근

  @GetMapping("/list")
  public List<FileResponseDto> list(
      @RequestParam(value = "prefix", required = false) String prefix,
      @RequestParam(value = "max", required = false, defaultValue = "100") int max
  ) {
    return s3.list(prefix, max);
  }

  @GetMapping("/download")
  public ResponseEntity<Void> download(
      @RequestParam("key") String key,
      @RequestParam(value = "filename", required = false) String filename
  ) {
    String bucket = props.getS3().getBucket();
    String name = (filename != null && !filename.isBlank())
        ? filename
        : Paths.get(key).getFileName().toString();

    GetObjectRequest getReq = GetObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .responseContentDisposition("attachment; filename=\"" + name + "\"")
        .build();

    GetObjectPresignRequest preReq = GetObjectPresignRequest.builder()
        .getObjectRequest(getReq)
        .signatureDuration(Duration.ofMinutes(5))
        .build();

    String signed = presigner.presignGetObject(preReq).url().toString();
    return ResponseEntity.status(302).location(URI.create(signed)).build();
  }
}