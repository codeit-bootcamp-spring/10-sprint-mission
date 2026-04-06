package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.Instant;

public record ReadStatusUpdateRequest(
    @NotNull(message = "읽은 시점 데이터는 필수 입력 사항입니다.")
    @PastOrPresent(message = "읽은 시점은 미래 시간일 수 없습니다.")
    Instant newLastReadAt
) {

}
