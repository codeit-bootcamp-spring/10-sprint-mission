package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.*;

@Getter
public class Channel extends MutableEntity {
    private final Set<User> participants;
    private final List<Message> messages;
    private String title;
    private String description;

    public Channel(String title, String description) {
        super();
        this.participants = new HashSet<>();
        this.messages = new ArrayList<>();
        this.title = title;
        this.description = description;
    }

    // participants
    public Set<User> getParticipants() {
        return Collections.unmodifiableSet(this.participants);
    }

    public void addParticipant(User user) {
        participants.add(user);
    }

    public void removeParticipant(User user) {
        participants.remove(user);
    }

    public void updateParticipant(User user) {
        if (this.participants.removeIf(u -> Objects.equals(u.getId(), user.getId()))) {
            addParticipant(user);
        }
    }

    // messages
    public List<Message> getMessages() {
        return Collections.unmodifiableList(this.messages);
    }

    public void addMessage(Message message) {
        this.messages.add(message);
    }

    public void removeMessage(Message message) {
        this.messages.remove(message);
    }

    public void updateMessage(Message message) {
        for (int i = 0; i < this.messages.size(); i++) {
            if (this.messages.get(i).getId().equals(message.getId())) {
                this.messages.set(i, message);
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


