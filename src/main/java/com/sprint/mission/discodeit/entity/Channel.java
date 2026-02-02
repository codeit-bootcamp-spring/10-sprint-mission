package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.*;

@Getter
public class Channel extends MutableEntity {
    private final Set<UUID> participants;
    private final List<UUID> messages;
    private String title;
    private String description;
    private ChannelType channelType;

    public Channel(ChannelType channelType, String title, String description) {
        super();
        this.participants = new HashSet<>();
        this.messages = new ArrayList<>();
        this.channelType = channelType;
        this.title = title;
        this.description = description;
    }

    // participants
    public Set<UUID> getParticipants() {
        return Collections.unmodifiableSet(this.participants);
    }

    public void addParticipant(UUID userId) {
        participants.add(userId);
    }

    public void removeParticipant(UUID userId) {
        participants.remove(userId);
    }

    public void updateParticipant(UUID userId) {
        if (this.participants.removeIf(u -> Objects.equals(u, userId))) {
            addParticipant(userId);
        }
    }

    // messages
    public List<UUID> getMessages() {
        return Collections.unmodifiableList(this.messages);
    }

    public void addMessage(UUID messageId) {
        this.messages.add(messageId);
    }

    public void removeMessage(UUID messageId) {
        this.messages.remove(messageId);
    }

    public void updateMessage(UUID messageId) {
        for (int i = 0; i < this.messages.size(); i++) {
            if (this.messages.get(i).equals(messageId)) {
                this.messages.set(i, messageId);
                break;
            }
        }
    }


    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format("'채널이름: %s / 채널설명:%s'", getTitle(), getDescription());
    }
}


