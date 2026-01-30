package com.sprint.mission.discodeit.entity;

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
    private final List<Channel> channels = new ArrayList<>();

    private UUID binaryContentId;


    @Getter
    private String name;
    @Getter
    private String email;
    @Getter
    private String password;

    // constructor
    public User(String name, String email, UUID binaryContentId, String password) {
        super();
        this.name = Objects.requireNonNull(name, "이름은 필수입니다.");
        this.email = Objects.requireNonNull(email, "이메일은 필수입니다.");
        this.password = Objects.requireNonNull(password, "비밀번호는 필수입니다.");
        this.binaryContentId = binaryContentId;
    }

    // Getter, update
    public List<Channel> getChannels() {
        return List.copyOf(this.channels);
    }     // 채널 객체를 직접 전달

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
    }


    // 채널 참여 시 채널 목록에 채널 추가
    public void joinChannel(Channel channel) {
        if (channel == null)
            return;
        if (!this.channels.contains(channel))
            this.channels.add(channel);
    }

    // 퇴장 시 삭제
    public void leaveChannel(Channel channel) {
        this.channels.remove(channel);
    }
}
