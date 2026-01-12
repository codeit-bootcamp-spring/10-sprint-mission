package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message extends BaseEntity {
    // id, createdAt, updatedAt 상속 받음
    private String content;
    private final UUID senderId; // User 객체참조로 수정 채널
    private final UUID channelId;
    // sender, channel은 수정 불가능

    // [] 유효성 검사 메서드 추가하기

    // BaseEntity() 상속 받음
    public Message(String content, UUID senderId, UUID channelId) {
        super(); // id, createdAt, updatedAt -> 생성자로 초기화;
        // 메시지 글자 제한 2,000자, 데스트톱 및 iOS 사용자는 4,000자까지 -> 2,000자로 설정
        // -> 유효성 검사 메서드 따로 분리
        this.content = content;
        this.senderId = senderId;
        this.channelId = channelId;
    }

    // Getter
    public String getContent() { return content; }
    public UUID getSenderId() { return senderId; }
    public UUID getChannelId() { return channelId; }

    // update
    public void updateContent(String newContent) {
        this.content = newContent;
        this.updateTimestamp();
    }
    // senderId, channerId는 수정 불가능

//    @Override
//    public String toString() {
//        return "Message{" +
//                "id=" + id +
//                ", createdAt=" + createdAt +
//                ", updatedAt=" + updatedAt +
//
//                ", content='" + content +
//                ", sender=" + sender.toString() +
//                '}';
//    }
}