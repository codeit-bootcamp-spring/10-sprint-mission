package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.*;

public class Channel extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String type;
    private final Set<User> users = new HashSet<>();
    private final List<Message> messages = new ArrayList<>();

    public Channel(String name, String type) {
        super();
        this.name = name;
        this.type = type;
    }
    public String getName() {
        return name;
    }
    public String getType() {
        return type;
    }
    public List<Message> getMessages() { return messages; }
    public Set<User> getUsers() { return users; }

    public void addMessage(Message message) { messages.add(message); }
    public void addUser(User user) { users.add(user); }

    public void updateName(String name) {
        this.name = name;
        updateTimestamps();
    }
    public void updateType(String type) {
        this.type = type;
        updateTimestamps();
    }

    public void removeMessage(Message message) { messages.remove(message); }
    public void removeUser(User user) { users.remove(user); }

    @Override
    public String toString() {
        return "채널[이름: " + name +
                ", 타입: " + type + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Channel channel = (Channel) o;
        return java.util.Objects.equals(getId(), channel.getId());
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(getId());
    }
}
