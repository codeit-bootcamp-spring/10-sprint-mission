package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message extends BaseEntity {
    private String content;
    private final UUID userId;    // 작성자
    private final UUID channelId; // 채널

    public Message(String content, UUID userId, UUID channelId) {
        super(); // id, createdAt, updatedAt -> 생성자로 초기화;
        // 메시지 글자 제한 2,000자, 데스트톱 및 iOS 사용자는 4,000자까지 -> 2,000자로 설정
        this.content = content;
        this.userId = userId;
        this.channelId = channelId;
    }

    // Getter
    public String getContent() { return content; }
    public UUID getUserId() { return userId; }
    public UUID getChannelId() { return channelId; }

    // update
    public void updateContent(String newContent) {
        this.content = newContent;
        this.updateTimestamp();
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", userId=" + userId +
                ", channelId=" + channelId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}