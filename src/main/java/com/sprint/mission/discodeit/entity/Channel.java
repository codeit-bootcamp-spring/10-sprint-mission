package com.sprint.mission.discodeit.entity;

import java.util.*;

public class Channel extends BaseEntity {
    private User owner;
    private ChannelType channelType;
    private String channelName;
    private String channelDescription;

    // 연관 관계
    // 해당 채널에 참여 중인 유저 List
    private final List<User> channelMembersList;
    // 해당 채널에 존재하는 메시지 List
    private final List<Message> channelMessagesList; // 채팅창 안의 메시지들

    // 생성자
    public Channel(User user, ChannelType channelType, String channelName, String channelDescription) {
        this.owner = user; // owner 임명(생성하는 사용자 본인)
        this.channelType = channelType;
        this.channelName = channelName;
        this.channelDescription = channelDescription;
        channelMembersList = new ArrayList<>();
        channelMessagesList = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Channel{" +
                "channelId = " + getId() + ", " +
//                "createdAt = " + getCreatedAt() + ", " +
//                "updatedAt = " + getUpdatedAt() + ", " +
                "owner = " + owner.getId() + ", " +
                "channel type = " + channelType + ", " +
                "channelName = " + channelName + ", " +
//                "channelDescription = " + channelDescription + ", " +
//                "channelMembersList = " + channelMembersList + ", " +
//                "channelMessagesList = " + channelMessagesList +
                "}";
    }

    // Getter
    public User getOwner() {
        return owner;
    }

    public ChannelType getChannelType() {
        return channelType;
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
        return channelMessagesList.stream().toList();
    }

    // update
    public void updateChannelName(String channelName) {
        this.channelName = channelName;
        updateTime();
    }

    public void updateChannelType(ChannelType channelType) {
        this.channelType = channelType;
        updateTime();
    }

    public void updateChannelDescription(String channelDescription) {
        this.channelDescription = channelDescription;
        updateTime();
    }

    // owner 변경(+업데이트)
    public void changeOwner(User owner) {
        this.owner = owner;
        updateTime();
    }

    // 채널 멤버 추가
    public void addMember(User user) {
        this.channelMembersList.add(user);
        updateTime();
    }

    // 채널 멤버 삭제
    public void removeMember(UUID userId) {
        this.channelMembersList.removeIf(user -> user.getId().equals(userId));
        updateTime();
    }

    // 채널에 메시지 추가
    public void addMessage(Message message) {
        this.channelMessagesList.add(message);
        updateTime();
    }

    // 채널에서 메시지 삭제
    public void removeMessageInChannel(UUID messageId) {
        this.channelMessagesList.removeIf(message -> message.getId().equals(messageId));
        updateTime();
    }

}
