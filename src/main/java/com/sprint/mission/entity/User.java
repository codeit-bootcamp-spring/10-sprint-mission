package com.sprint.mission.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class User extends BaseEntity {
    private final Set<UUID> channelIds = new HashSet<>();
    private String nickName;

    public User(String nickName) {
        super();
        this.nickName = getValidatedTrimmedInput(nickName);
    }

    public void joinChannel(UUID channelId) {
        channelIds.add(channelId);
    }

    public void leaveChannel(UUID channelId) {
        channelIds.remove(channelId);
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

    public Set<UUID> getChannelIds() {
        return channelIds;
    }
}
