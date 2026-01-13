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
    public void addParticipant(User user) {
        if (!participants.add(user)) {
            throw new IllegalStateException("이미 참가한 참가자입니다");
        }
    }
    public void removeParticipant(User user) {
        if (!participants.remove(user)) {
            throw new IllegalStateException("참여하지 않은 참가자입니다");
        }
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


