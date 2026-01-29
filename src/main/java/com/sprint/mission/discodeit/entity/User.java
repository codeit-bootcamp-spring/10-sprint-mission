package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.*;

@Getter
public class User extends BaseEntity {
    private String username;
    private String password;
    private String email;
    private UUID profileId;
    private List<UUID> messages;

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;

        this.messages = new ArrayList<>();
    }

    public void updateProfileId(UUID profileId) {
        this.profileId = profileId;
    }

    public void update(
            String username,
            String password,
            String email,
            UUID profileId
    ) {
        Optional.ofNullable(username)
                .ifPresent(value -> this.username = value);
        Optional.ofNullable(password)
                .ifPresent(value -> this.password = value);
        Optional.ofNullable(email)
                .ifPresent(value -> this.email = value);
        Optional.ofNullable(profileId)
                .ifPresent(value -> this.profileId = value);

        markUpdated();
    }

    public void addMessage(UUID messageId) {
        messages.add(messageId);
        markUpdated();
    }


    public void removeMessage(Message message) {
        messages.remove(message);
        markUpdated();
    }
}
