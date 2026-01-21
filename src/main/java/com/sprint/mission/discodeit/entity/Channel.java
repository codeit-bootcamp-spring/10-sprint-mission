package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Channel extends BaseEntity {
    private User owner;
    private String name;
    private Set<User> members;
    private List<Message> messages;

    public Channel(User user, String name) {
        this.owner = user;
        this.name = name;
        this.members = new HashSet<>();
        this.members.add(user);
        this.messages = new ArrayList<>();
        user.addChannel(this);
    }

    public User getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public Set<User> getMembers() {
        return members;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void updateName(String name) {
        this.name = name;
        markUpdated();
    }

    public void addMember(User user) {
        members.add(user);
        user.addChannel(this);
        markUpdated();
    }

    public void addMessage(Message message) {
        if (!messages.contains(message)) {
            messages.add(message);
        }

        if (message.getChannel() != this) {
            message.addChannel(this);
        }
        markUpdated();
    }

    public void clear() {
        for (User member : new ArrayList<>(members)) {
            removeMember(member);
        }

        for (Message message : new ArrayList<>(messages)) {
            message.detachFromUser();
        }

        messages.clear();
    }

    public void removeMember(User member) {
        members.remove(member);

        if (member.hasChannel(this)) {
            member.removeChannel(this);
        }

        markUpdated();
    }

    public void removeMessage(Message message) {
        messages.remove(message);
        markUpdated();
    }

    public void validateChannelOwner(User user) {
        if (!this.isOwner(user)) {
            throw new IllegalArgumentException("채널 소유자가 아닙니다 userId: " + user.getId());
        }
    }

    public void validateChannelMember(User user) {
        if (!this.hasMember(user)) {
            throw new IllegalArgumentException("채널 멤버가 아닙니다. userId: " + user.getId());
        }
    }

    public boolean isOwner(User user) {
        return owner.equals(user);
    }

    public boolean hasMember(User member) {
        return members.contains(member);
    }

    @Override
    public String toString() {
        return "Channel{" +
                "ownerName=" + owner.getNickName() +
                ", name='" + name + '\'' +
                ", membersSize=" + members.size() +
                ", messagesSize=" + messages.size() +
                '}';
    }
}
