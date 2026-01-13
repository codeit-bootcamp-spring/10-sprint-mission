package com.sprint.mission.discodeit.entity;

import java.util.*;

public class Channel extends BaseEntity {
    private User owner;
    private Boolean isPrivate; // True = Private, False = Public
    private String channelName;
    private String channelDescription = "";

    // 연관 관계
    // 해당 채널에 참여 중인 유저 목록
    private final Set<User> channelMembersList; // 유저 중복 참가 불가
    // 해당 채널에 존재하는 메시지 목록
    private final List<Message> channelMessagesList; // 채팅창 안의 메시지들

    // 생성자
    public Channel(User owner, Boolean isPrivate, String channelName, String channelDescription) {
        this.owner = owner; // owner 임명(생성하는 사용자 본인)
        owner.ownChannel(this); // owner의 객체에도 반영
        this.isPrivate = isPrivate;
        this.channelName = channelName;
        this.channelDescription = channelDescription;
        channelMembersList = new HashSet<>();
//        owner.joinChannel(this);
        channelMessagesList = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Channel{" +
                "channelId = " + getId() + ", " +
//                "createdAt = " + getCreatedAt() + ", " +
//                "updatedAt = " + getUpdatedAt() + ", " +
                "owner = " + owner + ", " +
                "isPrivate = " + isPrivate + ", " +
                "channelName = " + channelName + ", " +
//                "channelDescription = " + channelDescription + ", " +
                "channelMembers = " + channelMembersList + ", " +
                "channelMessages = " + channelMessagesList +
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

    public List<User> getChannelMembersList() {
        return channelMembersList.stream().toList();
    }

    public List<Message> getChannelMessagesList() {
        return channelMessagesList;
    }

    // update
    public void updateChannelName(String channelName) {
        this.channelName = channelName;
        updateTime();
    }

    public void updateIsPrivate(Boolean isPrivate) {
        this.isPrivate = !this.isPrivate;
        updateTime();
    }

    public void updateChannelDescription(String channelDescription) {
        this.channelDescription = channelDescription;
        updateTime();
    }

    // owner 변경(+업데이트)
    public void changeOwner(Channel channel, User owner) {
        this.owner.removeChannelOwner(channel); // 기존 채널 주인 소유권 제거
        this.owner = owner;
        updateTime();
        owner.ownChannel(this);
    }

    // owner 삭제
    public void removeOwner(User user) {
        this.owner = null; // ???????
        updateTime();
    }

    // 채널 멤버 추가
    public void addChannelMembers(User user) {
        this.channelMembersList.add(user);
        updateTime();
    }

    // 채널 멤버 삭제
    public void removeChannelMembers(User user) {
        this.channelMembersList.remove(user);
        updateTime();
    }

    // 채널에 메시지가 작성됨
    public void addChannelMessages(Message message) {
        this.channelMessagesList.add(message);
        updateTime();
    }
}
