package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User {
    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;

    private String username;
    // 채널 참여 내역과 메시지 전송 내역을 기록하는 필드
    private List<Channel> joinedChannels;
    private List<Message> sentMessages;

    public User(String username) {
        // id 자동생성 및 초기화
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        // username 초기화
        this.username = username;
        // 참여한 채널들과 보낸 메세지들
        this.joinedChannels = new ArrayList<>();
        this.sentMessages = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    // username 수정 메서드
    public void updateUsername(String username) {
        this.username = username;
        this.updatedAt = System.currentTimeMillis();
    }

    public UUID getUserId() {
        return id;
    }

    public List<Channel> getJoinedChannels() {
        return joinedChannels;
    }

    public List<Message> getSentMessages() {
        return sentMessages;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", username='" + username + '\'' +
                '}';
    }
}
