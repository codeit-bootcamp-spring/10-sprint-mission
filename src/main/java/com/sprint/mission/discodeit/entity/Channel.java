package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Channel extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private IsPrivate isPrivate;
    private UUID ownerId; // 채널 주인
    private String description; // 채널 소개
    private List<UUID> userIds;   // 채널 멤버
    private List<UUID> messageIds; // 채널에서 주고받은 메시지들

    public Channel(String name, IsPrivate isPrivate, UUID ownerId, String description) {
        super(UUID.randomUUID(), Instant.now());
        this.name = name;
        this.isPrivate = isPrivate;
        this.description = description;
        this.userIds = new ArrayList<>();
        this.messageIds = new ArrayList<>();
        addOwnerId(ownerId);
    }

    public UUID getLastMessageId(){
        if (messageIds.isEmpty()) {
            return null;
        }
        return messageIds.get(messageIds.size() - 1);
    }

    public void addMessage(UUID messageId) {
        if (messageId == null) {
            return;
        }
        if(!messageIds.contains(messageId)) {
            messageIds.add(messageId);
        }
    }

    public void addUserId(UUID userId) {
        if (!userIds.contains(userId)) {
            userIds.add(userId);
        }
    }

    public void addOwnerId(UUID ownerId){
        this.ownerId = ownerId;
        addUserId(ownerId);
    }

    public void updateOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
        this.onUpdate();
    }

    public void updateName(String name) {
        this.name = name;
        this.onUpdate();
    }

    public void updatePrivate(IsPrivate isPrivate) {
        this.isPrivate = isPrivate;
        this.onUpdate();
    }

    @Override
    public String toString() {
        return "채널명 : " + name + ", 공개여부 : " + isPrivate;
    }

}
