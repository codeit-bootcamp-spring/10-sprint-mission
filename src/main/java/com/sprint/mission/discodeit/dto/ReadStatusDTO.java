package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.time.Instant;
import java.util.UUID;

public class ReadStatusDTO {
    public record Create(
            UUID userId,
            UUID channelId
    ) {}

    public record Response(
            UUID id,
            UUID userId,
            UUID channelId,
            Instant lastReadAt
    ) {
        public static Response of(ReadStatus status) {
            return new Response(
                    status.getId(),
                    status.getUserId(),
                    status.getChannelId(),
                    status.getLastReadAt()
            );
        }
    }

    public record Update(
            UUID userId,
            UUID channelId
    ) {}
}

