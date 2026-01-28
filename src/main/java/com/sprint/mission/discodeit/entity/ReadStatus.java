package com.sprint.mission.discodeit.entity;

import com.fasterxml.jackson.databind.ser.Serializers;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatus extends BaseEntity {
    private UUID userID;
    private UUID channelID;
    private Instant lastReadAt = Instant.EPOCH;

    public boolean isRead(){
        return lastReadAt.isAfter(Instant.EPOCH);
    }


}
