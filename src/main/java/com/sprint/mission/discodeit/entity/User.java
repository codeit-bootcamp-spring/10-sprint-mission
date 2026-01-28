package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.*;

public class User extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @Getter
    private String username;
    @Getter
    private String email;
    @Getter
    private String password;
    @Getter
    private UUID profileId;
    private final Set<UUID> channelIds;

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.channelIds = new HashSet<>();
    }

    public void addChannel(UUID channelId) {
        this.channelIds.add(channelId);
    }

    public void deleteChannel(UUID channelId) {
        this.channelIds.remove(channelId);
    }

    public List<UUID> getChannelIds() {
        return new ArrayList<>(channelIds);
    }

    public void updateUsername(String username) {
        this.username = username;
        setUpdateAt();
    }

    public void updateEmail(String email) {
        this.email = email;
        setUpdateAt();
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
        setUpdateAt();
    }

    public void updateProfileId(UUID profileId) {
        this.profileId = profileId;
        setUpdateAt();
    }

    @Override
    public String toString() {
        return username + "/" + email;
    }
}
