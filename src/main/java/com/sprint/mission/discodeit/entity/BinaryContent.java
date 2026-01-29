package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.UUID;

@Getter
public class BinaryContent{
    private final UUID id;

    public BinaryContent() {
        this.id = UUID.randomUUID();
    }
}
