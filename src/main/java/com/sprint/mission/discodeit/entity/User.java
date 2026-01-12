package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private UserStatus status;
    private List<Message> messages;

    public User(String name, UserStatus status) {
        super(UUID.randomUUID(), System.currentTimeMillis());
        this.name = name;
        this.status = status;
        this.messages = new ArrayList<Message>();
    }

    public void addMessages(Message message) {
        this.messages.add(message);
        if (message.getSender() != this) {
            message.addUser(this);
        }
    }

    public String getName() {
        return name;
    }

    public UserStatus getStatus() {
        return status;
    }

    public List<Message> getMessages(){
        return messages;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateStatus(UserStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "유저명 : " + name + ", 상태 : " + status;
    }
}
