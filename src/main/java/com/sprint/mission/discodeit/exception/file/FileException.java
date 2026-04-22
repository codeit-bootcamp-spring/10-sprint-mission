package com.sprint.mission.discodeit.exception.file;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.nio.file.Path;
import java.util.Map;

public class FileException extends DiscodeitException {

  public FileException(ErrorCode errorCode) {
    super(errorCode);
  }

  public FileException(ErrorCode errorCode, Map<String, Object> detail) {
    super(errorCode, detail);
  }

  public FileException(ErrorCode errorCode, Path path) {
    super(errorCode, Map.of("path", path));
  }

  public FileException(ErrorCode errorCode, Exception exception) {
    super(errorCode, exception);
  }
}
