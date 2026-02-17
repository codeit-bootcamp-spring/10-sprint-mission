package com.sprint.mission.discodeit.channel.entity;

import lombok.Getter;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Channel implements Serializable {
    private static final long serialVersionUID = 1L;
    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    private ChannelType type;
    private String name;
    private String description;
    private UUID ownerId;
    private List<UUID> participantUserIds = new ArrayList<>();


    public Channel(ChannelType type, String name, String description, UUID ownerId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.type = type;
        this.name = name;
        this.description = description;
        this.ownerId = ownerId;
        this.participantUserIds.add(this.ownerId);
    }

    public Channel(ChannelType type, String name , String description,
                   List<UUID> userList, UUID ownerId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.type = type;
        this.name = name;
        this.description = description;
        this.ownerId = ownerId;
        this.participantUserIds = new ArrayList<>(userList);
        if (!this.participantUserIds.contains(ownerId)) {
            this.participantUserIds.add(ownerId);
        }
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
            this.updatedAt = Instant.now();
        }
    }
}