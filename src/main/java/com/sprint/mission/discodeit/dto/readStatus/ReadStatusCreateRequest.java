package com.sprint.mission.discodeit.dto.readStatus;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class ReadStatusCreateRequest {
    @NotNull(message = "유저 ID는 필수입니다")
    private UUID userId;

    @NotNull(message = "채널 ID는 필수입니다")
    private UUID channelId;

    @NotNull(message = "생성 시간은 필수입니다.")
    @PastOrPresent(message = "읽은 시간은 현재 또는 과거여야 합니다.")
    private Instant lastReadAt;
}
