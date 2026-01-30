package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class User extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String email;
    private String password;
    private List<UUID> messageIds;
    private List<UUID> channelIds;
    private UUID profileId;

    public User(String name, String email, String password, UUID profileId) {
        super(UUID.randomUUID(), Instant.now());
        this.name = name;
        this.email = email;
        this.password = password;
        this.messageIds = new ArrayList<>();
        this.channelIds = new ArrayList<>();
        this.profileId = profileId;
    }

    public void addMessages(UUID messageId) {
        this.messageIds.add(messageId);
    }

    public void addChannel(UUID channelId) {
        if (!this.channelIds.contains(channelId)) {
            this.channelIds.add(channelId);
        }
    }

    public void updateProfileId(UUID id) {
        this.profileId = id;
    }
    public void updateName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "유저명 : " + name;
    }


}
