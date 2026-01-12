package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message {
    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;

    // 메시지 내용
    private String content;
    // 유저 정보
    private User sentUser;
    // 채널 정보
    private Channel sentChannel;

    public Message(User sentUser, Channel sentChannel, String content) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        // content 초기화
        this.content = content;
        // sentUserId 초기화는 메시지 전송 시점에 설정
        this.sentUser = sentUser;
        // sentChannelId 초기화는 메시지 전송 시점에 설정
        this.sentChannel = sentChannel;
    }

    public UUID getId() {
        return id;
    }

    public UUID getSentUserId() {
        return sentUser.getUserId();
    }

    public UUID getSentChannelId() {
        return sentChannel.getChannelId();
    }

    public String getContent() {
        return content;
    }

    public void updateContent(String content) {
        this.content = content;
        this.updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", content='" + content + '\'' +
                ", sentUserId='" + sentUser + '\'' +
                ", sentChannelId='" + sentChannel + '\'' +
                '}';
    }
}
