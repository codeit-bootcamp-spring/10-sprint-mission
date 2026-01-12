package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;

public class User extends CommonEntity{
    private String userName;
    private String password;
    private String email;
    private List<Channel> channels;

    public User(String userName, String password, String email) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.channels = new ArrayList<>();
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
        return channels;
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

    public void updateChannels(List<Channel> channels) {
        this.channels = channels;
        this.updateAt = System.currentTimeMillis();
    }
}
