package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.event.FileUploadEvent;
import com.sprint.mission.discodeit.event.handler.FileUploadEventResultHandler;
import com.sprint.mission.discodeit.event.FileUploadResult;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class FileUploadEventListener {

  private final BinaryContentStorage binaryContentStorage;
  private final FileUploadEventResultHandler resultHandler;

  @Async("fileUploadExecutor")
  @TransactionalEventListener
  public void handleFileUpload(FileUploadEvent event) {
    List<FileUploadResult> results = binaryContentStorage.putAll(event.data());
    resultHandler.handleSuccess(results);
    resultHandler.handleFailures(results);
  }
}
