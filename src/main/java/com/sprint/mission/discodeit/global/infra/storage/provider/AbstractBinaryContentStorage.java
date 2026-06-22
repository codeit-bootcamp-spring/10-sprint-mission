package com.sprint.mission.discodeit.global.infra.storage.provider;

import com.sprint.mission.discodeit.binarycontent.domain.provider.BinaryContentStorage;
import com.sprint.mission.discodeit.binarycontent.domain.provider.FileUploadResult;
import com.sprint.mission.discodeit.global.log.ServiceLogAround;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractBinaryContentStorage implements BinaryContentStorage {

  protected final Executor fileUploadWorker;
  protected final String internalPath;

  protected CompletableFuture<FileUploadResult> putAsync(Entry<UUID, byte[]> entry) {
    return CompletableFuture.supplyAsync(() -> {
      try {
        int randomSeconds = new Random().nextInt(4);
        Thread.sleep(randomSeconds * 1000L);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
      return this.put(entry.getKey(), entry.getValue());
    }, fileUploadWorker).handle(FileUploadResult::of);
  }

  @Override
  public List<FileUploadResult> putAll(Map<UUID, byte[]> files) {
    List<CompletableFuture<FileUploadResult>> futures = files.entrySet().stream()
        .map(this::putAsync)
        .toList();
    return futures.stream()
        .map(CompletableFuture::join)
        .toList();
  }

  @Override
  @ServiceLogAround
  public String downloadUrl(UUID id) {
    existsOrThrow(id);
    return resolveUrl(id);
  }

  protected abstract void existsOrThrow(UUID id);

  protected abstract String resolveUrl(UUID id);
}
