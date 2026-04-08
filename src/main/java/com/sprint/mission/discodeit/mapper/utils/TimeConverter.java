package com.sprint.mission.discodeit.mapper.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

public final class TimeConverter {

  private TimeConverter() {
  }

  public static LocalDateTime toDateTime(Instant time) {
    return Optional.ofNullable(time)
        .map(t -> LocalDateTime.ofInstant(t, ZoneId.systemDefault()))
        .orElse(null);
  }

  public static Instant toInstant(LocalDateTime datetime) {
    return Optional.ofNullable(datetime)
        .map(t -> t.atZone(ZoneId.of("Asia/Seoul")).toInstant())
        .orElse(null);
  }
}
