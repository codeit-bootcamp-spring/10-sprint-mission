package com.sprint.mission.discodeit.entity;

import lombok.AccessLevel;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class User extends BaseEntity {
    private String username;
    private String email;
    @Getter(AccessLevel.NONE)
    private transient String password;
    // 채널 참여 내역과 메시지 전송 내역을 기록하는 필드
    private List<Channel> joinedChannels;
    private List<Message> sentMessages;

    public User(String username, String email, String password) {
        // id 자동생성 및 초기화
        super();
        // 필드 초기화
        this.username = username;
        this.email = email;
        this.password = password;
        // 참여한 채널들과 보낸 메세지들
        this.joinedChannels = new ArrayList<>();
        this.sentMessages = new ArrayList<>();
    }

    // username 수정 메서드
    public void updateUsername(String username) {
        this.username = username;
        super.setUpdatedAt();
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
                ", email=" + email +
                '}';
    }
}
