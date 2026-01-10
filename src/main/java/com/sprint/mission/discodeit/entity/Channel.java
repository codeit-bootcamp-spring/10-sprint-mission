package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.*;

public class Channel {
    private final UUID id; // 객체 식별을 위한 id
    private final Long createdAt; // 객체 생성 시간(유닉스 타임스탬프)
    private Long updatedAt; // 객체 수정 시간(유닉스 타임스탬프)

    private User owner;
    private Boolean isPrivate; // True = Private, False = Public
    private String channelName;
    private String channelDescription;

    // 해당 채널에 참여 중인 유저 목록
    private final Set<User> channelMembers; // 유저 중복 참가 불가
    // 해당 채널에 존재하는 메시지 목록
    private final List<Message> channelMessages; // 채팅창 안의 메시지들

    // 생성자
    public Channel(User owner, Boolean isPrivate, String channelName, String channelDescription) {
        // `id` 초기화
        this.id = UUID.randomUUID();
        // `createdAt` 초기화
        this.createdAt = Instant.now().toEpochMilli();
        this.updatedAt = this.createdAt;

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
                "id = " + id + ", " +
                "createdAt = " + createdAt + ", " +
                "updatedAt = " + updatedAt + ", " +
                "owner = " + owner + ", " +
                "isPrivate = " + isPrivate + ", " +
                "channelName = " + channelName + ", " +
                "channelDescription = " + channelDescription + ", " +
                "channelMembers = " + channelMembers +
                "}";
    }

    // Getter

    public UUID getId() {
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

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

    // update 시간 메소드
    public void updateTime() {
        this.updatedAt = Instant.now().toEpochMilli();
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
