package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;

public class Channel extends BaseEntity implements Serializable {
    private static final long serialVersion = 1L;

    private String name;
    private String description;

    public Channel(String name, String description) {
//        this.id = UUID.randomUUID();
//        this.createdAt = System.currentTimeMillis();
//        this.updatedAt = this.createdAt;

        this.name = name;
        this.description = description;
    }

    public void updateChannel (String name, String description) {
        this.name = name;
        this.description = description;
    }
    public void updateChannelName(String name) {
        this.name = name;
    }

    public void updateChannelDescription(String description) {
        this.description = description;
    }


    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }



}

