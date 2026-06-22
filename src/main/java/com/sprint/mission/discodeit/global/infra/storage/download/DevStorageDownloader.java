package com.sprint.mission.discodeit.global.infra.storage.download;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Profile("dev")
@Component
public class DevStorageDownloader implements StorageDownloader {

  @Value("${app.storage.local.root-path}")
  private String rootPath;

  @Override
  public ResponseEntity<?> download(String path) {
    String id = path.split("/")[2];
    Resource file = new FileSystemResource(String.join("/", rootPath, id));
    return ResponseEntity.status(HttpStatus.OK).body(file);
  }
}
