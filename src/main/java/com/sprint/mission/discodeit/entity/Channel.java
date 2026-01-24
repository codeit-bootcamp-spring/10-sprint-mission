package com.sprint.mission.discodeit.entity;

import java.util.*;

public class Channel extends DefaultEntity {
    private static final long serialVersionUID = 1L;
    private String channelName;
    private String channelDescription;

    private List<UUID> messages = new ArrayList<>();
    private List<UUID> roles = new ArrayList<>();

    public Channel(String channelName, String channelDescription) {
        this.channelName = channelName;
        this.channelDescription = channelDescription;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getChannelDescription() {
        return channelDescription;
    }

    public void updateChannelName(String channelName) {
        this.channelName = channelName;
        this.updatedAt = System.currentTimeMillis();
    }

    public void updateChannelDescription(String channelDescription) {
        this.channelDescription = channelDescription;
        this.updatedAt = System.currentTimeMillis();
    }

    public List<UUID> getMessagesID() {
        return messages;
    }

    public void AddMessageInChannel(UUID messageID) {
        messages.add(messageID);
    }

    public void DeleteMessageInChannel(UUID messageID) {
        messages.stream()
                .filter(message -> messageID.equals(message))
                .findFirst()
                .ifPresent(messages::remove);
    }

    public void setMessagesID(List<UUID> messages) {
        this.messages = messages;
    }

    public List<UUID> getRolesID() {
        return roles;
    }

    public void setRolesID(List<UUID> roles) {
        this.roles = roles;
    }

    public void AddRoleInChannel(UUID roleID) {
        roles.add(roleID);
    }

    public void DeleteRoleInChannel(UUID roleID) {
        roles.stream()
                .filter(role -> roleID.equals(role))
                .findFirst()
                .ifPresent(roles::remove);
    }

    public String toString() {
        return "채널명: " + channelName + ", 설명: " + channelDescription;
    }

}