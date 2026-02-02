package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class User extends BaseEntity {
    private String username;
    private String email;
    private final String password;
    // 채널 참여 내역과 메시지 전송 내역을 기록하는 필드
    private List<UUID> joinedChannelIds;
    private List<UUID> sentMessageIds;
    // 사용자의 프로필 이미지
    private UUID profileImageId;

    public User(String username, String email, String password, UUID profileImageId) {
        // id 자동생성 및 초기화
        super();
        // 필드 초기화
        this.username = username;
        this.email = email;
        this.password = password;
        // 참여한 채널들과 보낸 메세지들
        this.joinedChannelIds = new ArrayList<>();
        this.sentMessageIds = new ArrayList<>();
        // 사용자의 프로필 이미지
        this.profileImageId = profileImageId;
    }

    // username 수정 메서드
    public void updateUsername(String username) {
        this.username = username;
        super.setUpdatedAt();
    }

    public void updateJoinedChannels(UUID channelId) {
        joinedChannelIds.add(channelId);
    }

    public void removeChannel(UUID channelId) {
        joinedChannelIds.remove(channelId);
    }

    public void updateSentMessages(UUID messageId) {
        sentMessageIds.add(messageId);
    }

    public void removeSentMessage(UUID messageId) {
        sentMessageIds.remove(messageId);
    }

    public void updateProfileImage(UUID profileImageId) {
        this.profileImageId = profileImageId;
        super.setUpdatedAt();
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
