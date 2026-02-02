package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ReadStatus;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatusDto {
    private UUID id;
    private UUID channelId;
    private UUID userId;
    private Instant lastReadAt;

    public ReadStatusDto(ReadStatus readStatus) {
        this.id = readStatus.getId();
        this.channelId = readStatus.getChannelId();
        this.userId = readStatus.getUserId();
        this.lastReadAt = readStatus.getLastReadAt();
    }
}
