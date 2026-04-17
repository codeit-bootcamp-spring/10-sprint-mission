package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class StorageGetFailedException extends BinaryContentException {

  public StorageGetFailedException() {
    super(ErrorCode.STORAGE_GET_FAILED);
  }
}
