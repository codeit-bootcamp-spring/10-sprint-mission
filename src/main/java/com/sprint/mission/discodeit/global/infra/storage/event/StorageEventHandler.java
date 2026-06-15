package com.sprint.mission.discodeit.global.infra.storage.event;

import com.sprint.mission.discodeit.binarycontent.domain.event.FileUploadEvent;
import com.sprint.mission.discodeit.binarycontent.domain.provider.FileUploadResult;
import com.sprint.mission.discodeit.binarycontent.domain.provider.BinaryContentStorage;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class StorageEventHandler {

  private final BinaryContentStorage binaryContentStorage;
  private final StorageCallback resultHandler;

  @Async("fileUploadExecutor")
  @TransactionalEventListener
  public void handleFileUpload(FileUploadEvent event) {
    List<FileUploadResult> results = binaryContentStorage.putAll(event.data());
    resultHandler.handleSuccess(results);
    resultHandler.handleFailures(results);
  }
}
