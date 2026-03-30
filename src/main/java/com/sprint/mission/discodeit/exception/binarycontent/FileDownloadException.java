package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class FileDownloadException extends BinaryContentException {

  public FileDownloadException(Map<String, Object> details) {
    super(ErrorCode.FILE_DOWNLOAD_ERROR, details);
  }
}
