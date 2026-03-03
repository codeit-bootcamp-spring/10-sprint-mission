package com.sprint.mission.discodeit.common.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public interface TimeConverter {
    static LocalDateTime toDateTime(Instant time) {
        return LocalDateTime.ofInstant(time, ZoneId.systemDefault());
    }

    static Instant toInstant(LocalDateTime datetime) {
        return datetime.atZone(ZoneId.systemDefault()).toInstant();
    }
}
