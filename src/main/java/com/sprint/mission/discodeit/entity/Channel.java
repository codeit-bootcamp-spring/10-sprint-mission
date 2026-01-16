package com.sprint.mission.discodeit.entity;

import java.util.*;

public class Channel extends BaseEntity {
    private UUID ownerId;
    private ChannelType channelType;
    private String channelName;
    private String channelDescription;

    // 연관 관계
    // 해당 채널에 참여 중인 유저 Ids
    private final Set<UUID> channelMembersIds; // 유저 중복 참가 불가
    // 해당 채널에 존재하는 메시지 Ids
    private final List<UUID> channelMessagesIds; // 채팅창 안의 메시지들

    // 생성자
    public Channel(UUID userId, ChannelType channelType, String channelName, String channelDescription) {
        this.ownerId = userId; // owner 임명(생성하는 사용자 본인)
        this.channelType = channelType;
        this.channelName = channelName;
        this.channelDescription = channelDescription;
        channelMembersIds = new HashSet<>();
        channelMessagesIds = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Channel{" +
                "channelId = " + getId() + ", " +
//                "createdAt = " + getCreatedAt() + ", " +
//                "updatedAt = " + getUpdatedAt() + ", " +
                "owner ID = " + ownerId + ", " +
                "channel type = " + channelType + ", " +
                "channelName = " + channelName + ", " +
//                "channelDescription = " + channelDescription + ", " +
                "channelMembersIds = " + channelMembersIds + ", " +
                "channelMessagesIds = " + channelMessagesIds +
                "}";
    }

    // Getter
    public UUID getOwnerId() {
        return ownerId;
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

    public List<UUID> getChannelMembersIds() {
        return channelMembersIds.stream().toList();
    }

    public List<UUID> getChannelMessagesList() {
        return channelMessagesIds.stream().toList();
    }

    // update
    public void updateChannelName(String channelName) {
        this.channelName = channelName;
        updateTime();
    }

    public void updateIsChannelType(ChannelType channelType) {
        this.channelType = channelType;
        updateTime();
    }

    public void updateChannelDescription(String channelDescription) {
        this.channelDescription = channelDescription;
        updateTime();
    }

    // owner 변경(+업데이트)
    public void changeOwner(UUID ownerId) {
        this.ownerId = ownerId;
        updateTime();
    }

    // 채널 멤버 Ids 추가
    public void addMember(UUID userId) {
        this.channelMembersIds.add(userId);
        updateTime();
    }

    // 채널 멤버 Ids 삭제
    public void removeMember(UUID userId) {
        this.channelMembersIds.remove(userId);
        updateTime();
    }

    // 채널에 메시지 Ids 추가
    public void addMessage(UUID messageId) {
        this.channelMessagesIds.add(messageId);
        updateTime();
    }

    // 채널에서 메시지 Ids 삭제
    public void removeMessageInChannel(UUID messageId) {
        this.channelMessagesIds.remove(messageId);
        updateTime();
    }

}
