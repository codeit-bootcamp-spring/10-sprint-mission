package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.event.FileUploadResult;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Predicate;
import org.springframework.beans.factory.annotation.Qualifier;

public abstract class BaseBinaryContentStorage implements BinaryContentStorage {

  protected final Executor fileUploadWorker;
  protected final String internalPath;

  protected BaseBinaryContentStorage(
      @Qualifier("fileUploadWorker") Executor fileUploadWorker,
      String internalPath) {
    this.fileUploadWorker = fileUploadWorker;
    this.internalPath = internalPath;
  }

  protected <T> void throwOrNot(T value, Predicate<T> condition,
      Function<T, DiscodeitException> exception) {
    if (!condition.test(value)) {
      throw exception.apply(value);
    }
  }

  protected CompletableFuture<FileUploadResult> putAsync(Entry<UUID, byte[]> entry) {
    return CompletableFuture.supplyAsync(() -> this.put(entry.getKey(), entry.getValue()),
        fileUploadWorker).handle(FileUploadResult::of);
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

  protected String convertToInternalPath(String path) {
    return String.join("/", internalPath, path);
  }
}
