package com.sprint.mission.discodeit.entity;

import java.util.*;

public class Channel extends BaseEntity {
    private String channelName;
    private User owner;
    private final List<Message> messages = new ArrayList<>();
    private final List<ChannelUserRole> channelUserRoles = new ArrayList<>();

    public Channel(String channelName, User owner) {
        super();
        validateChannelName(channelName);
        validateOwnerId(owner.getId());

        this.channelName = channelName;
        this.owner = owner;
    }

    private void validateChannelName(String channelName) {
        if (channelName == null || channelName.trim().isEmpty()) {
            throw new IllegalArgumentException("channelName은 null 이거나 비어있을 수 없습니다.");
        }
    }
    private void validateOwnerId(UUID ownerId) {
        if(ownerId==null) {
            throw new IllegalArgumentException("채널 소유자(OWNER)는 필수로 존재해야 합니다.");
        }
    }

    public String getChannelName() { return this.channelName; }
    public User getOwner() { return this.owner; }
    public List<Message> getMessages() { return new ArrayList<>(messages); }
    public List<ChannelUserRole> getChannelUserRoles() { return new ArrayList<>(channelUserRoles); }

    public void updateChannelName(String channelName) {
        this.channelName = channelName;
        this.updateTimestamp();
    }
    public void updateOwner(User newOwner) {
        validateOwnerId(newOwner.getId());
        if(this.owner.getId().equals(newOwner.getId())) {
            throw new IllegalArgumentException("본인을 제외한 새로운 OWNER을 지정해야 합니다.");
        }
        this.owner = newOwner;
        this.updateTimestamp();
    }
    public void addMessage(Message message) { this.messages.add(message); }
    public void removeMessage(Message message) { this.messages.remove(message); }
    public void addChannelUserRole(ChannelUserRole channelUserRole) { this.channelUserRoles.add(channelUserRole); }
    public void removeChannelUserRole(ChannelUserRole channelUserRole) { this.channelUserRoles.remove(channelUserRole); }
}