package com.sprint.mission.discodeit.global.infra.storage.event;

import com.sprint.mission.discodeit.binarycontent.domain.provider.FileUploadResult;
import java.util.List;

public interface StorageCallback {

  void handleSuccess(List<FileUploadResult> results);

  void handleFailures(List<FileUploadResult> results);
}
