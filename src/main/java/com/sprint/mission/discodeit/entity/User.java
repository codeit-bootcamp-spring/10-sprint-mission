package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.*;
import lombok.Getter;

@Getter
public class User extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String email;
    private String password;
    private final List<Message> messages = new ArrayList<>();
    private final Set<Channel> channels = new HashSet<>();
    private UUID profileId;

    public User(String name, String email, String password, UUID profileId) {
        super();
        this.name = name;
        this.email = email;
        this.password = password;
        this.profileId = profileId;
    }

    public User(String name, String email, UUID profileId) {
        this(name, email, null, profileId);
    }

    public User(String name, String email) {
        this(name, email, null, null);
    }


    public void addMessage(Message message) { messages.add(message);
    }
    public void addChannel(Channel channel) {
        this.channels.add(channel);
    }

    public void updateName(String name) {
        this.name = name;
        updateTimestamps();
    }
    public void updateEmail(String email) {
        this.email = email;
        updateTimestamps();
    }
    public void updateProfileId(UUID profileId) {
        this.profileId = profileId;
        updateTimestamps();
    }
    public void removeMessage(Message message) { this.messages.remove(message); }
    public void removeChannel(Channel channel) { this.channels.remove(channel); }

    @Override
    public String toString() {
        return "유저[이름: " + name +
                ", 이메일: " + email + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return java.util.Objects.equals(getId(), user.getId());
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(getId());
    }
}
