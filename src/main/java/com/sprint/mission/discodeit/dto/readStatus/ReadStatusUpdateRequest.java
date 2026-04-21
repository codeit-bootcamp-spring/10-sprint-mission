package com.sprint.mission.discodeit.dto.readStatus;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatusUpdateRequest {

    @NotNull(message = "생성 시간은 필수입니다.")
    @PastOrPresent(message = "읽은 시간은 현재 또는 과거여야 합니다.")
    private Instant newLastReadAt;
}
