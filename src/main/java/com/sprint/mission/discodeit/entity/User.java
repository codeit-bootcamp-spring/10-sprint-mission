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
    private final List<Message> messages;

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.channels = new HashSet<>();
        this.messages = new ArrayList<>();
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

    public List<Message> getMessages() {
        return new ArrayList<>(messages);
    }

    public void join(Channel channel) {
        this.channels.add(channel);
    }

    public void send(Message message) {
        this.messages.add(message);
    }

    public void leave(Channel channel) {
        this.channels.remove(channel);
    }

    public void delete(Message message) {
        this.messages.remove(message);
    }

    public void updateUsername(String username) {
        this.username = username;
        setUpdateAt();
    }

    public void updateEmail(String email) {
        this.email = email;
        setUpdateAt();
    }

    @Override
    public String toString() {
        return username + "/" + email;
    }
}
