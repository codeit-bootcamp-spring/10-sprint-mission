package com.sprint.mission.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class User extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Set<Channel> channels;
    private final String email;
    private String name;

    public User(String name, String email) {
        super();
        this.channels = new HashSet<>();
        this.name = getValidatedTrimmedInput(name);
        this.email = getValidatedTrimmedInput(email);
    }

    public void joinChannel(Channel channel) {
        channels.add(channel);
    }

    public void leaveChannel(Channel channel) {
        channels.remove(channel);
    }

    public void updateName(String name) {
        this.name = getValidatedTrimmedInput(name);
        touch();
    }

    private String getValidatedTrimmedInput(String input) {
        validateContentIsNotBlank(input);
        return input.trim();
    }

    private void validateContentIsNotBlank(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("입력 값이 비어있을 수 없습니다.");
        }
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public List<Channel> getChannels() {
        return List.copyOf(channels);
    }
}
