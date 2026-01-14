package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;

public class User extends BaseEntity {
    private String username;
    private String password;
    private String email;
    private List<Channel> myChannels = new ArrayList<>();
    private List<Message> myMessages = new ArrayList<>();

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public void updateUsername(String newUsername){
        this.username = newUsername;
        this.setUpdatedAt(System.currentTimeMillis());
    }

    public void updateEmail(String newEmail){
        this.email = newEmail;
        this.setUpdatedAt(System.currentTimeMillis());
    }

    public void updatePassword(String newPassword){
        this.password = newPassword;
        this.setUpdatedAt(System.currentTimeMillis());
    }

    public List<Message> getMyMessages() {
        return myMessages;
    }

    public List<Channel> getMyChannels() {
        return myChannels;
    }

    public void addMessage(Message message){
        this.myMessages.add(message);
    }

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    @Override
    public String toString() {
        return "이름: " + username + ", 이메일: " + email + ", 비밀번호: " + password;
    }
}
