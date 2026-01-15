package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Channel extends BaseEntity {
    // === 1 필드 ===
    // id, createdAt, updatedAt 상속 받음
    private String channelName;
    private User owner ; // owner는 변경 가능하므로 final 사용 x
    private List<Message> messages = new ArrayList<>(); // 1. 채널에 작성된 메시지 목록
    private List<ChannelUserRole> channelUserRoles = new ArrayList<>(); // 2. 채널에 참여 중인 유저(Role 포함) 목록

    // === 2 생성자 ===
    // BaseEntity() 상속 받음
    public Channel(String channelName, User ownerUser) {
        super(); // id, createdAt, updatedAt -> 생성자로 초기화;
        validateChannelName(channelName);
        validateOwnerId(ownerUser.getId());

        this.channelName = channelName;
        this.owner = ownerUser;
    }

    // === 3 비즈니스 로직 ===
    private void validateChannelName(String channelName) {
        if(channelName == null || channelName.trim().isEmpty()) {
            throw new IllegalArgumentException("채널(서버) 이름은 필수입니다.");
        }
        // 채널 이름 관련 규칙 추가 (추후 구현)
    }
    private void validateOwnerId(UUID ownerId) {
        if (ownerId == null) {
            throw new IllegalArgumentException("채널 소유자(Owner ID)는 필수입니다.");
        }
    }

    // === 4 Getter ===
    public String getChannelName() {
        return channelName;
    }
    public User getOwner() {
        return owner;
    }
    public List<Message> getMessages() {
        return messages;
    }
    public List<ChannelUserRole> getChannelUserRoles() {
        return channelUserRoles;
    }
    // getId(), getCreatedAt(), getUpdatedAt()은 상속 받음

    // === 5 update ===
    public void updateChannelName(String newChannelName) {
        this.channelName = newChannelName;
        this.updateTimestamp();
    }
    public void updateOwner(User newOwner) {
        validateOwnerId(newOwner.getId());
        if(this.owner.getId().equals(newOwner.getId())) {
            return;
        }
        this.owner = newOwner;
        this.updateTimestamp();
    }
    public void addMessage(Message message) { // 채널에 메시지 추가
        this.messages.add(message);
    }
    public void removeMessage(Message message) { // 채널에서 메시지 제거
        this.messages.remove(message);
    }
    public void addChannelUserRole(ChannelUserRole role) { // 채널에 사용자(Role) 추가
        this.channelUserRoles.add(role);
    }
    public void removeChannelUserRole(ChannelUserRole role) { // 채널에서 사용자(Role) 제거
        this.channelUserRoles.remove(role);
    }
    // updateTimestamp()는 상속받음
}