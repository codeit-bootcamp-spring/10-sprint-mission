package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;

public class User extends CommonEntity{
    private static final long serialVersionUID = 1L;
    private String userName;
    private transient String password;
    private String email;
    private final List<Channel> channels = new ArrayList<>();
    private final List<Message> messages = new ArrayList<>();

    public User(String userName, String password, String email) {
        this.userName = userName;
        this.password = password;
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public List<Channel> getChannels() {
        return List.copyOf(channels);
    }

    public List<Message> getMessages() {
        return List.copyOf(messages);
    }

    public void updateUserName(String userName) {
        this.userName = userName;
        this.updateAt = System.currentTimeMillis();
    }

    public void updatePassword(String password) {
        this.password = password;
        this.updateAt = System.currentTimeMillis();
    }

    public void updateEmail(String email) {
        this.email = email;
        this.updateAt = System.currentTimeMillis();
    }

    public void addChannel(Channel channel) {
        channels.add(channel);
        this.updateAt = System.currentTimeMillis();
    }

    public void removeChannel(Channel channel) {
        channels.remove(channel);
        this.updateAt = System.currentTimeMillis();
    }

    public void addMessage(Message message) {
        messages.add(message);
        this.updateAt = System.currentTimeMillis();
    }

    public void removeMessage(Message message) {
        messages.remove(message);
        this.updateAt = System.currentTimeMillis();
    }
}
