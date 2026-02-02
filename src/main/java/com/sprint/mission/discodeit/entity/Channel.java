package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.*;

@Getter
public class Channel extends BaseEntity {
    private String name;
    private String description;
    private ChannelType channelType;
    private Set<UUID> memberIds;
    private List<UUID> messageIds;

    public Channel(
            String name,
            String description,
            ChannelType channelType,
            Set<UUID> memberIds
    ) {
        this.name = name;
        this.description = description;
        this.channelType = channelType;
        this.memberIds = new HashSet<>(memberIds);

        messageIds = new ArrayList<>();
    }

    public static Channel buildPublic(
            String name,
            String description
    ) {
        return new Channel(
                name,
                description,
                ChannelType.PUBLIC,
                new HashSet<>()
        );
    }

    public static Channel buildPrivate(
            Set<UUID> memberIds
    ) {
        return new Channel(
                null,
                null,
                ChannelType.PRIVATE,
                new HashSet<>(memberIds) // 방어적 복사
        );
    }

    public boolean isPublic() {
        return channelType.isPublic();
    }

    public boolean isPrivate() {
        return channelType.isPrivate();
    }

    public boolean hasMember(UUID memberId) {
        return memberIds.contains(memberId);
    }

    public void updateInfo(String name, String description) {
        Optional.ofNullable(name)
                .ifPresent(value -> this.name = value);
        Optional.ofNullable(description)
                .ifPresent(value -> this.description = value);

        markUpdated();
    }

    public void removeMessage(UUID messageId) {
        messageIds.remove(messageId);
        markUpdated();
    }

    public void addMember(UUID memberId) {
        memberIds.add(memberId);
        markUpdated();
    }

    public void addMessage(UUID messageId) {
        messageIds.add(messageId);
        markUpdated();
    }

    public void validateChannelMember(UUID memberId) {
        if (!this.hasMember(memberId)) {
            throw new IllegalArgumentException("채널 멤버가 아닙니다. memberId: " + memberId);
        }
    }
}
