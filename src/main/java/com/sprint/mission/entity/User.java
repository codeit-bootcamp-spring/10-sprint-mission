package com.sprint.mission.entity;

import java.util.*;

public class User extends BaseEntity {
    private final Set<Channel> channels;
    private String nickName;

    public User(String nickName) {
        super();
        this.channels = new HashSet<>();
        this.nickName = getValidatedTrimmedInput(nickName);
    }

    public void joinChannel(Channel channel) {
        channels.add(channel);
    }

    public void leaveChannel(Channel channel) {
        channels.remove(channel);
    }

    public void updateNickName(String nickName) {
        this.nickName = getValidatedTrimmedInput(nickName);
        touch();
    }

    private String getValidatedTrimmedInput(String input) {
        validateContentIsNotBlank(input);
        return input.trim();
    }

    private void validateContentIsNotBlank(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("닉네임이 비어있을 수 없습니다.");
        }
    }

    public String getNickName() {
        return nickName;
    }

    public List<Channel> getChannels() {
        return List.copyOf(channels);
    }
}
