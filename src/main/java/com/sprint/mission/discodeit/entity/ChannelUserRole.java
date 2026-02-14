package com.sprint.mission.discodeit.entity;

public class ChannelUserRole extends BaseEntity {
    private final Channel channel;
    private final User user;
    private ChannelRole role;

    public ChannelUserRole(Channel channel, User user, ChannelRole role) {
        super();
        validateChannel(channel);
        validateUser(user);
        validateChannelRole(role);

        this.channel = channel;
        this.user = user;
        this.role = role;
    }

    private void validateChannel(Channel channel) {
        if(channel==null) {
            throw new IllegalArgumentException("유저-채널 관계에서 채널은 필수로 존재해야 합니다.");
        }
    }
    private void validateUser(User user) {
        if(user==null) {
            throw new IllegalArgumentException("유저-채널 관계에서 유저는 필수로 존재해야 합니다.");
        }
    }
    private void validateChannelRole(ChannelRole role) {
        if(role==null) {
            throw new IllegalArgumentException("유저-채널 관계에서 유저는 필수로 권한(Role)을 가져야 합니다.");
        }
    }

    public Channel getChannel() { return channel; }
    public User getUser() { return user; }
    public ChannelRole getChannelRole() { return role; }

    public void updateRole(ChannelRole newRole) {
        validateChannelRole(newRole);
        this.role = newRole;
        this.updateTimestamp();
    }
}
