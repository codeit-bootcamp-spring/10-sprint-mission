package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.UserDto;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class User extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    // 유저가 속한 채널 목록
    private final List<UUID> channelIds = new ArrayList<>();

    @Getter
    private UUID binaryContentId;

    @Getter
    private String name;
    @Getter
    private String email;
    @Getter
    private String password;

    // constructor
    public User(UserDto.UserRequest request, UUID binaryContentId) {
        super();
        this.name = Objects.requireNonNull(request.name(), "이름은 필수입니다.");
        this.email = Objects.requireNonNull(request.email(), "이메일은 필수입니다.");
        this.password = Objects.requireNonNull(request.password(), "비밀번호는 필수입니다.");
        this.binaryContentId = binaryContentId;
    }

    // Getter, update
    public List<UUID> getChannels() {
        return this.channelIds;
    }

    public void updateName(String name) {
        this.name = name;
        updateTimestamp();
    }

    public void updateEmail(String email) {
        this.email = email;
        updateTimestamp();
    }

    public void updatePassword(String password) {
        this.password = password;
        updateTimestamp();
    }

    public void updateBinaryContentId(UUID binaryContentId) {
        this.binaryContentId = binaryContentId;
        updateTimestamp();
    }


    // 채널 참여 시 채널 목록에 채널 추가
    public void joinChannel(Channel channel) {
        if (channel == null)
            return;
        if (!this.channelIds.contains(channel.getId()))
            this.channelIds.add(channel.getId());
    }

    // 퇴장 시 삭제
    public void leaveChannel(Channel channel) {
        this.channelIds.remove(channel.getId());
    }

    @Override
    public String toString() {
        return "이름: " + this.name +
                "\n 이메일: " + this.email +
                "\n 비밀번호: " + this.password;
    }
}

