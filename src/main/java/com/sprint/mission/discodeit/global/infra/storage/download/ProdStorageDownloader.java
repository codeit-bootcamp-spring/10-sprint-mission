package com.sprint.mission.discodeit.global.infra.storage.download;

import com.sprint.mission.discodeit.global.infra.storage.constant.StorageHeaders;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Profile("prod")
@Component
public class ProdStorageDownloader implements StorageDownloader {

  @Override
  public ResponseEntity<?> download(String path) {
    return ResponseEntity.status(HttpStatus.OK)
        .header(StorageHeaders.X_ACCEL_REDIRECT, path)
        .build();
  }
}
