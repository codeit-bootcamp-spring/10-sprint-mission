package com.sprint.mission.discodeit.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

import java.util.*;

@Getter
@JsonPropertyOrder({"id", "createdAt", "updatedAt", "type", "name", "description"})
public class Channel extends BaseEntity {
    private String name;
    private String description;
    private ChannelType type;
    @JsonIgnore
    private Set<UUID> memberIds;
    @JsonIgnore
    private List<UUID> messageIds;

    public Channel(
            String name,
            String description,
            ChannelType type,
            Set<UUID> memberIds
    ) {
        this.name = name;
        this.description = description;
        this.type = type;
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
                new HashSet<>(memberIds)
        );
    }

    @JsonIgnore
    public boolean isPublic() {
        return type.isPublic();
    }

    @JsonIgnore
    public boolean isPrivate() {
        return type.isPrivate();
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

    public void addMessage(UUID messageId) {
        messageIds.add(messageId);
        markUpdated();
    }

    public void removeMessage(UUID messageId) {
        messageIds.remove(messageId);
        markUpdated();
    }
}
