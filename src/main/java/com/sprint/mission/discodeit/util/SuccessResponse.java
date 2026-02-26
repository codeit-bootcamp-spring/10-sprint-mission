package com.sprint.mission.discodeit.util;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SuccessResponse<T> {

  private String message;
  private T data;
  private Instant timestamp;

  public static <T> SuccessResponse<T> success(T data) {
    return new SuccessResponse<>("요청에 성공하였습니다.", data, Instant.now());
  }

  public static <T> SuccessResponse<T> failure(String message) {
    return new SuccessResponse<>(message, null, Instant.now());
  }
}
