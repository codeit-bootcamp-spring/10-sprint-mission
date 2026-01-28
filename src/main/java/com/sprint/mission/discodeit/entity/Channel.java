package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Channel extends CommonEntity{
    private static final long serialVersionUID = 1L;
    private String channelName;
    private ChannelType channelType;
    private String description;
    private final List<UUID> messageIds = new ArrayList<>();
    private final List<UUID> userIds = new ArrayList<>();

    public Channel(String channelName, ChannelType channelType, String description) {
        this.channelName = channelName;
        this.channelType = channelType;
        this.description = description;
    }

    public List<UUID> getMessageIds() {
        return List.copyOf(messageIds);
    }

    public List<UUID> getUserIds() {
        return List.copyOf(userIds);
    }

    public void updateChannelName(String channelName) {
        this.channelName = channelName;
        this.updateAt = System.currentTimeMillis();
    }

    public void updateChannelType(ChannelType channelType) {
        this.channelType = channelType;
        this.updateAt = System.currentTimeMillis();
    }

    public void updateDescription(String description) {
        this.description = description;
        this.updateAt = System.currentTimeMillis();
    }

    public void addMessageId(UUID messageId) {
        messageIds.add(messageId);
        this.updateAt = System.currentTimeMillis();
    }

    public void removeMessageId(UUID messageId) {
        messageIds.remove(messageId);
        this.updateAt = System.currentTimeMillis();
    }

    public void addUserId(UUID userId) {
        userIds.add(userId);
        this.updateAt = System.currentTimeMillis();
    }

    public void removeUserId(UUID userId) {
        userIds.remove(userId);
        this.updateAt = System.currentTimeMillis();
    }
}
