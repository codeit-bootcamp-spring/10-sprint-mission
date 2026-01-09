package com.sprint.mission.discodeit.entity;

public class User extends Entity {
    private String userId;

    public User(String userId) {
        super();
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public void update(String userId) {
        this.userId = userId;
        updateTime();
    }
}
