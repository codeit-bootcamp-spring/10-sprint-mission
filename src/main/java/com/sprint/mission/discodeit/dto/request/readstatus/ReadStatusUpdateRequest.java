package com.sprint.mission.discodeit.dto.request.readstatus;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.Instant;

@Builder
public record ReadStatusUpdateRequest(
        @NotNull(message = "마지막 읽음 시간은 필수 입력값입니다.")
        Instant newLastReadAt,

        @NotNull(message = "특정 채널에 대한 알림 활성화 여부는 필수 입력값입니다.")
        boolean notificationEnabled
) {

}
