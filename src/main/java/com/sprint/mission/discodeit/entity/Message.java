package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message extends BaseEntity {
    // 메시지 내용
    private String content;
    // 유저 정보
    private User sentUser;
    // 채널 정보
    private Channel sentChannel;

    public Message(User sentUser, Channel sentChannel, String content) {
        // id 자동생성 및 초기화
        super();
        // content 초기화
        this.content = content;
        // sentUserId 초기화는 메시지 전송 시점에 설정
        this.sentUser = sentUser;
        // sentChannelId 초기화는 메시지 전송 시점에 설정
        this.sentChannel = sentChannel;
    }

    public User getSentUser() {
        return sentUser;
    }

    public Channel getSentChannel() {
        return sentChannel;
    }

    public String getContent() {
        return content;
    }

    public void updateContent(String content) {
        this.content = content;
        setUpdatedAt();
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", content='" + content + '\'' +
                ", sentUser='" + sentUser + '\'' +
                ", sentChannel='" + sentChannel + '\'' +
                '}';
    }
}
