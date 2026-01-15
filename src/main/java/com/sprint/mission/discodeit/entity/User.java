package com.sprint.mission.discodeit.entity;

import java.util.*;

public class User extends BaseEntity{
    private String name;
    private String email;
    private final List<Message> messages = new ArrayList<>();
    private final Set<Channel> channels = new HashSet<>();

    public User(String name, String email) {
        super();
        this.name = name;
        this.email = email;
    }
    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }

    public void updateName(String name) {
                this.name = name;
                updateTimestamps();
    }
    public void updateEmail(String email) {
            this.email = email;
            updateTimestamps();
    }

    public List<Message> getMessages() { return messages; }
    public Set<Channel> getChannels() { return channels; }


    public void addMessage(Message message) { messages.add(message);
    }
    public void addChannel(Channel channel) {
        this.channels.add(channel);
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
