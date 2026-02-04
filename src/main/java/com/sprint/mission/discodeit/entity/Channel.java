package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;

@Getter
public class Channel extends BaseEntity {
    private ChannelType type;
    private String name;
    private String description;

    public Channel(ChannelType type, String name, String description) {
        super();
        this.type = type;
        this.name = name;
        this.description = description;
    }

    public void update(String newName, String newDescription) {
        boolean isAnyValueUpdated = false;
        if (newName != null && !newName.equals(this.name)) {
            this.name = newName;
            isAnyValueUpdated = true;
        }
        if (newDescription != null && !newDescription.equals(this.description)) {
            this.description = newDescription;
            isAnyValueUpdated = true;
        }

        if (isAnyValueUpdated) {
            updateInstant();
        }
    }
}
