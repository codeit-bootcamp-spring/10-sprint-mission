package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ReadStatus extends BaseEntity {
    private UUID userId;
    private UUID channelId;
    private ReadStatusType readStatusType;

    public ReadStatus(UUID userId, UUID channelId) {
        this.userId = userId;
        this.channelId = channelId;
        this.readStatusType = ReadStatusType.UNREAD;
    }

    public void updateReadStatusType() {
        if (this.readStatusType == ReadStatusType.UNREAD) {
            this.readStatusType = ReadStatusType.READ;
        } else {
            this.readStatusType = ReadStatusType.UNREAD;
        }
    }
}
