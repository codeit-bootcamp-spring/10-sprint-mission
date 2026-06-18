package com.sprint.mission.discodeit.dto.readStatus;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatusUpdateRequest {
    @PastOrPresent(message = "읽은 시간은 현재 또는 과거여야 합니다.")
    private Instant newLastReadAt;

    private Boolean newNotificationEnabled;
}
