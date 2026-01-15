package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message extends BaseEntity {
    // === 1 필드 ===
    // id, createdAt, updatedAt 상속 받음
    private String content;
    private final User sender;
    private final Channel channel;
    // sender, channel은 수정 불가능

    // === 2 생성자 ===
    // BaseEntity() 상속 받음
    public Message(String content, User sender, Channel channel) {
        super(); // id, createdAt, updatedAt -> 생성자로 초기화;
        // 메시지 글자 제한 2,000자, 데스트톱 및 iOS 사용자는 4,000자까지 -> 2,000자로 설정 (추후 구현)
        this.content = content;
        this.sender = sender;
        this.channel = channel;
    }

    // === 3 비즈니스 로직 ===
    // [] 메시지 내용 유효성 검사 메서드 추가 (추후 구현)

    // === 4 Getter ===
    public String getContent() {
        return content;
    }
    public UUID getSenderId() {
        return sender.getId();
    }
    public UUID getChannelId() {
        return channel.getId();
    }

    // === 5 update ===
    public void updateContent(String newContent) {
        this.content = newContent;
        this.updateTimestamp();
    }
    // senderId, channerId는 수정 불가능
}