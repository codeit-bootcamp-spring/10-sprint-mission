package com.sprint.mission.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; // 같은 메모리 주소면 true
        if (!(obj instanceof User)) return false; // 비교 대상이 User로 형변환(다운캐스팅)이 안되면 false
        User user = (User) obj; // User로 형변환
        return Objects.equals(this.id, user.id); // 실제 유저 id와 형변환 된 유저 id를 비교
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
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
