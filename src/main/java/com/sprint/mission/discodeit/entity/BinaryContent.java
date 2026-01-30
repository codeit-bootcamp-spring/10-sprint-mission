package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class BinaryContent extends BaseEntity{

    public BinaryContent(byte[] imageBytes) {
        super(UUID.randomUUID(), Instant.now());
    }

}
