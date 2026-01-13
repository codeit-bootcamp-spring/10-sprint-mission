package com.sprint.mission.entity;

import java.util.List;

public class User extends BaseEntity {
    private String nickName;
    private List<Channel> channels;

    public User(String nickName) {
        super();
        this.nickName = getValidatedTrimmedInput(nickName);
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
}
