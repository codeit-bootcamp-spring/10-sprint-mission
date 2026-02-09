package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class ReadStatus extends DefaultEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private final UUID userID;
    private final UUID channelID;
    private Instant lastUserReadTimeInChannel;

    public ReadStatus(UUID userID, UUID channelID) {
        this.userID = userID;
        this.channelID = channelID;
        this.lastUserReadTimeInChannel = Instant.now();
    }
}
