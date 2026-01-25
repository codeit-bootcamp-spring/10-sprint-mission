package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message extends DefaultEntity {
    private static final long serialVersionUID = 1L;
    private String message; // 메시지 내용
    private final UUID userID; // 보낸 사람
    private UUID channelID; //속한 채널

    public Message(UUID userID, String message, UUID channelID) {
        this.message = message;
        this.userID = userID;
        this.channelID = channelID;
    }

    public String getMessage() {
        return message;
    }

    public void updateMessage(String message) {
        this.message = message;
        updatedAt = System.currentTimeMillis();
    }

    public UUID getUserID() {
        return userID;
    }

    public UUID getChannelID() {
        return channelID;
    }

    public String toString() {
        return message;
    }
}
