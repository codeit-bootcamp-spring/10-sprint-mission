package com.sprint.mission.discodeit.entity;

public class ChannelUserRole extends BaseEntity {
    private final Channel channel;
    private final User user;
    private ChannelRole role;

    // 생성자
    public ChannelUserRole(Channel channel, User user, ChannelRole role) {
        super(); // id, createdAt, updatedAt 초기화
        validateChannelUser(channel, user, role);

        this.channel = channel;
        this.user = user;
        this.role = role;
    }

    // 유효성 검증
    private void validateChannelUser(Channel channel, User user, ChannelRole role) {
        if (channel == null) {
            throw new IllegalArgumentException("채널 정보는 필수");
        }
        if (user == null) {
            throw new IllegalArgumentException("유저 정보는 필수");
        }
        if (role == null) {
            throw new IllegalArgumentException("역할(Role) 정보는 필수");
        }
    }

    // Getter
    public Channel getChannel() {
        return channel;
    }
    public User getUser() {
        return user;
    }
    public ChannelRole getRole() {
        return role;
    }

    // update
    public void updateRole(ChannelRole newRole) {
        if (newRole == null) {
            throw new IllegalArgumentException("변경할 역할 정보가 없음");
        }
        this.role = newRole;
        this.updateTimestamp();
    }
}