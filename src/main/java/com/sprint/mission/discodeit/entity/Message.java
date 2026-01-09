package com.sprint.mission.discodeit.entity;

public class Message extends Entity {
    private String message;

    public Message(String message) {
        super();
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public void update(String message) {
        this.message = message;
        updateTime();
    }
}
