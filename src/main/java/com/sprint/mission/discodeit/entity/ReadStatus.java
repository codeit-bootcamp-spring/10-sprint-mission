package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatus extends DefaultEntity{
    private final UUID userID;
    private final UUID channelID;
    private Instant lastUserReadtimeInChannel;

    public ReadStatus(UUID userID, UUID channelID) {
        this.userID = userID;
        this.channelID = channelID;
    }

    public void userReadMessageInChannel(){
        this.lastUserReadtimeInChannel = Instant.now();
    }
}
