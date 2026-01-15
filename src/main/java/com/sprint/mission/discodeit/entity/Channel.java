package com.sprint.mission.discodeit.entity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Channel extends Common {
    private final Set<User> participants;
    private String title;
    private String description;

    public Channel(String title, String description) {
        super();
        this.participants = new HashSet<>();
        this.title = title;
        this.description = description;
    }

    // participants
    public Set<User> getParticipants() {
        return Collections.unmodifiableSet(this.participants);
    }
    public boolean addParticipant(User user) {
        return participants.add(user);
    }
    public boolean removeParticipant(User user) {
        return participants.remove(user);
    }

    public String getTitle() {
        return this.title;
    }
    public void updateTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }
    public void updateDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format("채널이름: %s\t채널설명:%s", getTitle(), getDescription());
    }
}


