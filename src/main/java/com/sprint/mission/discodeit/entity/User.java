package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User extends CommonEntity {
    private String userName;
    private String userEmail;
    private String userPassword;
    private final List<Message> messages = new ArrayList<>();
    private final List<Channel> joinedChannels = new ArrayList<>();

    public User(String userName, String userEmail) {
        this.userName = userName;
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void updateUserName(String userName) {
        this.userName = userName;
        update();
    }

    public void updateUserEmail(String userEmail) {
        this.userEmail = userEmail;
        update();
    }

    public List<Message> getMessages() {
        return messages;
    }

    public List<Channel> getJoinedChannels() {
        return joinedChannels;
    }


}
