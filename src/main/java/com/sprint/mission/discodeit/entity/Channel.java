package com.sprint.mission.discodeit.entity;

<<<<<<< HEAD
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class Channel implements Serializable {
    private static final long serialVersionUID = 1L;
    private UUID id;
    private Long createdAt;
    private Long updatedAt;
    //
    private ChannelType type;
    private String name;
    private String description;

    public Channel(ChannelType type, String name, String description) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now().getEpochSecond();
        //
        this.type = type;
=======
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

>>>>>>> upstream/김혜성
        this.name = name;
        this.description = description;
    }

<<<<<<< HEAD
    public UUID getId() {
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public ChannelType getType() {
        return type;
=======
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
>>>>>>> upstream/김혜성
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
<<<<<<< HEAD
    }

    public void update(String newName, String newDescription) {
        boolean anyValueUpdated = false;
        if (newName != null && !newName.equals(this.name)) {
            this.name = newName;
            anyValueUpdated = true;
        }
        if (newDescription != null && !newDescription.equals(this.description)) {
            this.description = newDescription;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now().getEpochSecond();
        }
=======
>>>>>>> upstream/김혜성
    }



}

