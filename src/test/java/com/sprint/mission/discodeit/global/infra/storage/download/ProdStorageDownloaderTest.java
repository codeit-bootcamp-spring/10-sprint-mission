package com.sprint.mission.discodeit.global.infra.storage.download;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.global.infra.storage.constant.StorageHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class ProdStorageDownloaderTest {

  @Test
  @DisplayName("download - X-Accel-Redirect 헤더와 함께 200 OK를 반환한다.")
  void download() {
    ProdStorageDownloader downloader = new ProdStorageDownloader();
    String path = "/internal/path/to/file.jpg";

    ResponseEntity<?> response = downloader.download(path);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getHeaders().getFirst(StorageHeaders.X_ACCEL_REDIRECT)).isEqualTo(path);
  }
}
