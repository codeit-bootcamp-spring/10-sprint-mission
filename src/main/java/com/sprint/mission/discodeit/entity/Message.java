package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message {
    private final UUID id;
    // 생성 시간은 수정될 수 없음(final)
    private final long createdAt;
    private long updatedAt;
    private String content;
    private User user;

    public Message(User user, String content) {
        this.id = UUID.randomUUID();
        this.user = user;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = createdAt;
        this.content = content;
    }

    public void addUser(User user) {
        this.user = user;
        if (!user.getMessages().contains(this)) {
            user.getMessages().add(this);
        }
    }

    // 각 필드를 반환하는 getter
    public UUID getId() {
        return id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public String getContent() {
        return content;
    }

    public User getUser() {
        return user;
    }

    // 필드를 수정하는 update 함수
    public void updateContent(String content) {
        this.content = content;
        setUpdatedAt();
    }

    public void setUpdatedAt() {
        this.updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return  "{content=" + content + '\'' +
                ", user=" + user.getUserName() +
                '}';
    }
}
