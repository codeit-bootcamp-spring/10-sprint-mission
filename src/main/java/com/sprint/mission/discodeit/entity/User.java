package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User extends BaseEntity {
    private String username;
    // 채널 참여 내역과 메시지 전송 내역을 기록하는 필드
    private List<Channel> joinedChannels;
    private List<Message> sentMessages;

    public User(String username) {
        // id 자동생성 및 초기화
        super();
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
        super.setUpdatedAt();
    }

    public List<Channel> getJoinedChannels() {
        return joinedChannels;
    }

    public List<Message> getSentMessages() {
        return sentMessages;
    }

    public void updateJoinedChannels(Channel channel) {
        joinedChannels.add(channel);
    }

    public void removeChannel(Channel channel) {
        joinedChannels.remove(channel);
    }

    public void updateSentMessages(Message message) {
        sentMessages.add(message);
    }

    public void removeSentMessage(Message message) {
        sentMessages.remove(message);
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
