package com.sprint.mission.discodeit.entity;

public class User extends DefaultEntity {
    private String userName;
    private RoleGroup groups;

    public User(String userName) {
        this.userName = userName;
        groups = null;
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
