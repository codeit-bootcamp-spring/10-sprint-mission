package com.sprint.mission.discodeit.entity;

import com.fasterxml.jackson.databind.ser.Serializers;

import java.time.Instant;
import java.util.UUID;


public class ReadStatus extends BaseEntity {
    private UUID userID;
    private UUID channelID;
    private Instant lastReadAt = Instant.EPOCH;
}
