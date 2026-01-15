package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class User extends BaseEntity {

    // 유저가 속한 채널 목록
    private final List<Channel> channels = new ArrayList<>();

    private String name;
    private String email;
    private String profileImageUrl;

    // constructor
    public User(String name, String email, String profileImageUrl) {
        super();
        this.name = Objects.requireNonNull(name, "이름은 필수입니다.");
        this.email = Objects.requireNonNull(email, "이메일은 필수입니다.");
        this.profileImageUrl = profileImageUrl;
    }

    // Getter, update
    public List<Channel> getChannels() {
        return List.copyOf(this.channels);
    }     // 채널 객체를 직접 전달

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }

    public String getProfileImageUrl() {
        return this.profileImageUrl;
    }

    public void updateName(String name) {
        this.name = name;
        updateTimestamp();
    }

    public void updateEmail(String email) {
        this.email = email;
        updateTimestamp();
    }

    public void updateProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
        updateTimestamp();
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
