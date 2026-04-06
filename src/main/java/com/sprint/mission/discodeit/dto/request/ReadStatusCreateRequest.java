package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusCreateRequest(
    @NotNull(message = "사용자 ID는 필수 입력 사항입니다.")
    UUID userId,

    @NotNull(message = "채널 ID는 필수 입력 사항입니다.")
    UUID channelId,

    @NotNull(message = "마지막 읽은 시점은 필수 입력 사항입니다.")
    Instant lastReadAt
) {

}
