package com.sprint.mission.discodeit.aws.controller;

import com.sprint.mission.discodeit.aws.service.S3UploadService;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class S3UploadController {

  private final S3UploadService s3;

  @PostMapping("/upload")
  public ResponseEntity<Void> upload(@RequestParam("file") MultipartFile file) {
    String url = s3.store(file);
    return ResponseEntity.created(URI.create(url)).build();
  }


}
