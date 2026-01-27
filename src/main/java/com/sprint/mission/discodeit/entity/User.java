package com.sprint.mission.discodeit.entity;

import java.util.*;

public class User extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private String username;
    private String email;
    private String password;
    private final Set<UUID> channelIds;

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.channelIds = new HashSet<>();
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
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

    @Override
    public String toString() {
        return username + "/" + email;
    }
}
