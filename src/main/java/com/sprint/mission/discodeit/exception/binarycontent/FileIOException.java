package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class FileIOException extends BinaryContentException {

  public FileIOException() {
    super(ErrorCode.FILE_IO_ERROR);

  }

}
