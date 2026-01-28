package com.sprint.mission.discodeit.entity;

import com.fasterxml.jackson.databind.ser.Serializers;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatus extends BaseEntity {
    private final UUID id;
    private final UUID userID;
    private final UUID channelID;
    private Instant lastReadAt;

    public ReadStatus(UUID userID, UUID channelID){
        this.id = UUID.randomUUID();
        this.userID = userID;
        this.channelID = channelID;
        this.lastReadAt = Instant.EPOCH;
    }

    public void read(){
        this.lastReadAt = Instant.now();
    }

    public boolean isRead(){
        return lastReadAt.isAfter(Instant.EPOCH);
    }


}
