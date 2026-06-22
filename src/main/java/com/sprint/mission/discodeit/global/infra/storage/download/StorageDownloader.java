package com.sprint.mission.discodeit.global.infra.storage.download;

import org.springframework.http.ResponseEntity;

public interface StorageDownloader {

  ResponseEntity<?> download(String path);
}
