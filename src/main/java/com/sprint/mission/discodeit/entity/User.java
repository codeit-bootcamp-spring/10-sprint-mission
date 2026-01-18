package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class User extends BaseEntity{
    private String username;
    private String email;
    private String password;
    private final Set<Channel> channels;

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.channels = new HashSet<>();
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

    public List<Channel> getChannels() {
        return new ArrayList<>(channels);
    }

    public void join(Channel channel) {
        this.channels.add(channel);
    }

    public void leave(Channel channel) {
        this.channels.remove(channel);
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
    }

    @Override
    public String toString() {
        return username + "/" + email;
    }
}
