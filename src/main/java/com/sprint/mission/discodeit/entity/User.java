package com.sprint.mission.discodeit.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class User extends Entity {
    private final String email;
    private String nickname;
    private final Set<UUID> channels;

    public User(String nickname, String email) {
        super();
        this.nickname = nickname;
        this.email = email;
        this.channels = new HashSet<>();
    }

    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }

    public Set<UUID> getChannels() {
        return new HashSet<>(channels);
    }

    public User updateUserNickname(String nickname) {
        super.update();
        this.nickname = nickname;
        return this;
    }

    public void join(UUID channelId) {
        channels.add(channelId);
    }

    public void leave(UUID channelId) {
        channels.remove(channelId);
    }

    @Override
    public String toString() {
        return String.format(
                "User [id=%s, nickname=%s, email=%s, joinedChannels=%s]",
                getId().toString().substring(0, 5),
                nickname,
                email,
                channels.stream()
                        .map(id -> id.toString().substring(0, 5))
                        .toList()
        );
    }

}
