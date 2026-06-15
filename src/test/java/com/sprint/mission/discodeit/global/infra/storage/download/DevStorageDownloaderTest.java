package com.sprint.mission.discodeit.global.infra.storage.download;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

class DevStorageDownloaderTest {

  private DevStorageDownloader downloader;

  @BeforeEach
  void setUp() {
    downloader = new DevStorageDownloader();
    ReflectionTestUtils.setField(downloader, "rootPath", "/test/root");
  }

  @Test
  @DisplayName("download - 파일 시스템 리소스를 200 OK와 함께 반환한다.")
  void download() {
    String path = "/internal/file-id-1234";

    ResponseEntity<?> response = downloader.download(path);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Resource body = (Resource) response.getBody();
    assertThat(body).isInstanceOf(FileSystemResource.class);
    assertThat(((FileSystemResource) body).getPath()).endsWith("file-id-1234");
  }
}
