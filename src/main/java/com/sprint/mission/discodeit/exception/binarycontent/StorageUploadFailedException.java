package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class StorageUploadFailedException extends BinaryContentException {

  public StorageUploadFailedException() {
    super(ErrorCode.STORAGE_UPLOAD_FAILED);
  }
}
