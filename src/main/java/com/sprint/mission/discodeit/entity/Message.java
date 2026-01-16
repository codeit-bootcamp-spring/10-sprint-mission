package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message extends BaseEntity {
    private final UUID channelId; // 메시지가 위치한 채널
    private final UUID authorId; // 메시지 작성자
    private String content; // 메시지 내용

    // 생성자
    public Message(UUID channelId, UUID authorId, String content) {
        this.channelId = channelId;
        this.authorId = authorId;
        this.content = content;
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageId = " + getId() + ", " +
                "channelId = " + channelId + ", " +
//                "createdAt = " + getCreatedAt() + ", " +
//                "updatedAt = " + getUpdatedAt() + ", " +
                "authorId = " + authorId + ", " +
                "content = " + content +
                "}";
    }

    // Getter
    public UUID getMessageChannelId() {
        return channelId;
    }

    public UUID getAuthorId() {
        return authorId;
    }

    public String getMessageContent() {
        return content;
    }

    // update
    public void updateContent(String content) {
        this.content = content;
        updateTime();
    }
}
