package com.sprint.mission.discodeit.dto.userStatus;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class UserStatusUpdateRequest {
    @NotNull(message = "최종 접속 시간은 필수입니다")
    @PastOrPresent(message = "최종 접속 시간은 현재 또는 과거여야 합니다.")
    private Instant newLastActiveAt;
}
