package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class User extends defaultEntity{
    private String userName;

    public User(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void updateUserName(String userName) {
        this.userName = userName;
        this.updatedAt = System.currentTimeMillis();
    }

    public String toString() {
        return userName;
    }
}
