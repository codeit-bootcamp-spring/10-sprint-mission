package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;

import java.time.Instant;
import java.util.UUID;

public class ReadStatusDto {

    public record ReadStatusRequest(
            UUID userId,
            UUID channelId
    ) {
    }

    public record ReadStatusResponse(
            UUID readStatusId,
            Instant lastReadTime,
            UUID userId,
            UUID channelId
    ) {
        public static ReadStatusResponse from(ReadStatus readStatus){
            return new ReadStatusResponse(
                    readStatus.getId(),
                    readStatus.getLastReadTime(),
                    readStatus.getUserId(),
                    readStatus.getChannelId()
            );
        }
    }
}
