package com.sprint.mission.discodeit.global.infra.storage.provider.local;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sprint.mission.discodeit.global.infra.storage.exception.local.FileException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.Executor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class LocalDiskBinaryContentStorageTest {

  @Mock
  private Executor fileUploadWorker;

  private LocalDiskBinaryContentStorage storage;

  @TempDir
  Path tempDir;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    storage = new LocalDiskBinaryContentStorage(fileUploadWorker, "/internal/local", tempDir);
  }

  @Test
  @DisplayName("init - 루트 디렉토리가 없으면 생성한다.")
  void init() {
    Path newRoot = tempDir.resolve("new-root");
    LocalDiskBinaryContentStorage newStorage = new LocalDiskBinaryContentStorage(fileUploadWorker, "/internal/local", newRoot);
    
    newStorage.init();

    assertThat(newRoot).exists();
  }

  @Test
  @DisplayName("put - 이미 존재하는 파일이면 예외가 발생한다.")
  void put_alreadyExists() throws IOException {
    UUID id = UUID.randomUUID();
    File file = new File(tempDir.toFile(), id.toString());
    file.createNewFile();

    assertThatThrownBy(() -> storage.put(id, new byte[]{1, 2, 3}))
        .isInstanceOf(FileException.class);
  }

  @Test
  @DisplayName("put - 존재하지 않는 파일이면 정상적으로 기록하고 ID를 반환한다.")
  void put_success() throws IOException {
    UUID id = UUID.randomUUID();

    UUID result = storage.put(id, new byte[]{1, 2, 3});

    assertThat(result).isEqualTo(id);
    Path filePath = tempDir.resolve(id.toString());
    assertThat(Files.readAllBytes(filePath)).containsExactly(1, 2, 3);
  }

  @Test
  @DisplayName("downloadUrl - 파일이 존재하면 internal path와 ID를 조합한 URL을 반환한다.")
  void downloadUrl_success() throws IOException {
    UUID id = UUID.randomUUID();
    File file = new File(tempDir.toFile(), id.toString());
    file.createNewFile();

    String url = storage.downloadUrl(id);

    assertThat(url).isEqualTo("/internal/local/" + id.toString());
  }

  @Test
  @DisplayName("downloadUrl - 파일이 존재하지 않으면 예외가 발생한다.")
  void downloadUrl_notFound() {
    UUID id = UUID.randomUUID();

    assertThatThrownBy(() -> storage.downloadUrl(id))
        .isInstanceOf(FileException.class);
  }
}
