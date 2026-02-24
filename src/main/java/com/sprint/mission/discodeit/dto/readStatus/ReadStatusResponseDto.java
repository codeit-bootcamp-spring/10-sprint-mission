package com.sprint.mission.discodeit.dto.readStatus;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusResponseDto(

        Instant createdAt, //사용자가 채널에 처음 참여했을때
        Instant updatedAt,//마지막으로 읽은 메시지의 시간
        UUID userId,
        UUID channelId
) {}
