package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message extends BaseEntity {
    private String content;
    private final UUID userId;    // 작성자
    private final UUID channelId; // 채널

    public Message(String content, UUID userId, UUID channelId) {
        super(); // ID 생성, 생성시간 기록
        this.content = content;
        this.userId = userId;
        this.channelId = channelId;
    }

    // Getter
    public String getContent() { return content; }
    public UUID getUserId() { return userId; }
    public UUID getChannelId() { return channelId; }

    // 메시지 수정
    public void updateContent(String newContent) {
        this.content = newContent;
        this.updateTimestamp(); // 수정 시간 갱신
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", userId=" + userId +
                ", channelId=" + channelId +
                ", createdAt=" + createdAt +
                '}';
    }
}