package com.sprint.mission.discodeit.entity;

import java.util.*;

public class Channel extends Common {
    private final Map<UUID,User> users;
    private final List<Message> messages;
    private String title;
    private String description;

    public Channel(String title, String description) {
        super();
        this.users = new HashMap<>();
        this.messages = new ArrayList<>();
        this.title = title;
        this.description = description;
    }

    // users
    public Map<UUID, User> getUsers() {
        return Collections.unmodifiableMap(this.users);
    }
    public void addUser(User user) {
        if (user == null) {
            throw new NullPointerException("User 인스턴스가 아닙니다");
        }
        if (users.containsKey(user.getId())) {
            throw new IllegalStateException("이미 참여한 사용자입니다");
        }
        users.put(user.getId(), user);
    }
    public void removeUser(User user) {
        if (user == null) {
            throw new NullPointerException("User 인스턴스가 아닙니다");
        }
        if (!users.containsKey(user.getId())) {
            throw new IllegalStateException("참여하지 않은 사용자입니다");
        }
        users.remove(user.getId());
    }

    // messages
    public List<Message> getMessages() {
        return Collections.unmodifiableList(this.messages);
    }
    public void addMessage(Message message) {
        messages.add(message);
    }
    public void removeMessage(Message message) {
        int idx = messages.indexOf(message);
        if (idx == -1) {
            throw new IllegalStateException("없는 메세지 입니다");
        }
        messages.remove(message);
    }

    public String getTitle() {
        return this.title;
    }
    void updateTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }
    void updateDescription(String description) {
        this.description = description;
    }
}


