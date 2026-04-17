package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class StorageDownloadFailedException extends BinaryContentException {

  public StorageDownloadFailedException() {
    super(ErrorCode.STORAGE_DOWNLOAD_FAILED);
  }
}
