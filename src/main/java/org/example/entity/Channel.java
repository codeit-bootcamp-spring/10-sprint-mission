package org.example.entity;

import java.util.ArrayList;
import java.util.List;

public class Channel extends BaseEntity {
    private String name;
    private String description;
    private ChannelType type;
    private List<User> members;
    private List<Message> messages;
    private User owner;

    public Channel(String name, String description, ChannelType type,  User owner) {
        super();
        this.name = name;
        this.description = description;
        this.type = type;
        this.members = new ArrayList<>();
        this.members.add(owner);
        this.messages = new ArrayList<>();  //messages;
        this.owner = owner;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
        this.updatedAt = System.currentTimeMillis();
    }

    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = System.currentTimeMillis();
    }

    public void setType(ChannelType type) {
        this.type = type;
        this.updatedAt = System.currentTimeMillis();
    }

    public void setMembers(List<User> members) {
        this.members = members;
        this.updatedAt = System.currentTimeMillis();
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
        this.updatedAt = System.currentTimeMillis();
    }

    public void setOwner(User owner) {
        this.owner = owner;
        this.updatedAt = System.currentTimeMillis();
    }

    //getter
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ChannelType getType() {
        return type;
    }
    public List<User> getMembers() {
        return members;
    }
    public List<Message> getMessages() {
        return messages;
    }
    public User getOwner() {
        return owner;
    }
}
