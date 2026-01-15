package com.sprint.mission.discodeit.entity;

public class ChannelUserRole extends BaseEntity {
    // === 1 필드 ===
    // id, createdAt, updatedAt 상속 받음
    // id(PK)
    private final Channel channel; // (FK)
    private final User user; // (FK)
    private ChannelRole role;

    // === 2 생성자 ===
    public ChannelUserRole(Channel channel, User user, ChannelRole role) {
        super(); // id, createdAt, updatedAt 초기화
        validateChannelUser(channel, user, role);

        this.channel = channel;
        this.user = user;
        this.role = role;
    }

    // === 3 비즈니스 로직 ===
    private void validateChannelUser(Channel channel, User user, ChannelRole role) {
        if (channel == null) {
            throw new IllegalArgumentException("채널 정보는 필수 입니다.");
        }
        if (user == null) {
            throw new IllegalArgumentException("유저 정보는 필수 입니다.");
        }
        if (role == null) {
            throw new IllegalArgumentException("권한/역할(Role) 정보는 필수 입니다.");
        }
    }

    // === 4 Getter ===
    public Channel getChannel() {
        return channel;
    }
    public User getUser() {
        return user;
    }
    public ChannelRole getRole() {
        return role;
    }
    // getId(), getCreatedAt(), getUpdatedAt()은 상속 받음

    // === 5 update ===
    public void updateRole(ChannelRole newRole) {
        if (newRole == null) {
            throw new IllegalArgumentException("변경할 권한/역할 정보가 없습니다.");
        }
        this.role = newRole;
        this.updateTimestamp();
    }
    // updateTimestamp()는 상속받음
}