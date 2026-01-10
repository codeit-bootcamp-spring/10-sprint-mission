package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.*;

public class Channel extends BaseEntity {
    private User owner;
    private Boolean isPrivate; // True = Private, False = Public
    private String channelName;
    private String channelDescription;

    // 연관 관계
    // 해당 채널에 참여 중인 유저 목록
    private final Set<User> channelMembers; // 유저 중복 참가 불가
    // 해당 채널에 존재하는 메시지 목록
    private final List<Message> channelMessages; // 채팅창 안의 메시지들

    // 생성자
    public Channel(User owner, Boolean isPrivate, String channelName, String channelDescription) {
        this.owner = owner;
        this.isPrivate = isPrivate;
        this.channelName = channelName;
        this.channelDescription = channelDescription;
        channelMembers = new HashSet<>();
        channelMessages = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Channel{" +
                "channelId = " + getId() + ", " +
                "createdAt = " + getCreatedAt() + ", " +
                "updatedAt = " + getUpdatedAt() + ", " +
                "owner = " + owner + ", " +
                "isPrivate = " + isPrivate + ", " +
                "channelName = " + channelName + ", " +
                "channelDescription = " + channelDescription + ", " +
                "channelMembers = " + channelMembers +
                "}";
    }

    // Getter
    public User getOwner() {
        return owner;
    }

    public Boolean getPrivate() {
        return isPrivate;
    }

    public Boolean getIsPrivate() {
        return isPrivate;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getChannelDescription() {
        return channelDescription;
    }

    public Set<User> getChannelMembers() {
        return channelMembers;
    }

    public List<Message> getChannelMessages() {
        return channelMessages;
    }

    // update
    public void updateOwner(User owner) {
        this.owner = owner;
        updateTime();
    }

    public void updateIsPrivate() {
        this.isPrivate = !this.isPrivate;
        updateTime();
    }

    public void updateChannelName(String channelName) {
        this.channelName = channelName;
        updateTime();
    }

    public void updateChannelDescription(String channelDescription) {
        this.channelDescription = channelDescription;
        updateTime();
    }

    public void updateChannelMembers(User user) {
        this.channelMembers.add(user);
        updateTime();
    }
}
