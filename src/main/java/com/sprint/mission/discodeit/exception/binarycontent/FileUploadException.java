package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class FileUploadException extends BinaryContentException {

  public FileUploadException(Map<String, Object> details, Throwable cause) {
    super(ErrorCode.FILE_UPLOAD_ERROR, details, cause);
  }
}
