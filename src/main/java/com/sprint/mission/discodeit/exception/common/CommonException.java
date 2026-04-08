package com.sprint.mission.discodeit.exception.common;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

public class CommonException extends DiscodeitException {

  public CommonException(ErrorCode errorCode) {
    super(errorCode);
  }

  public CommonException(ErrorCode errorCode, Map<String, Object> detail) {
    super(errorCode, detail);
  }

  public CommonException(ErrorCode errorCode, MultipartFile profile) {
    this(errorCode);
    addDetail(getErrorDetails(profile));
  }

  private Map<String, Object> getErrorDetails(MultipartFile profile) {
    Map<String, Object> details = new HashMap<>();
    details.put("fileName", profile.getName());
    details.put("size", profile.getSize());
    details.put("contentType", profile.getContentType());
    return details;
  }
}
