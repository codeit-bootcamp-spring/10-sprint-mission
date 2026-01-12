package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message extends BaseEntity{
    private String content;
    private User user;

    public Message(User user, String content) {
        super();
        this.user = user;
        this.content = content;
    }

    public void addUser(User user) {
        this.user = user;
        if (!user.getMessages().contains(this)) {
            user.getMessages().add(this);
        }
    }

    // 각 필드를 반환하는 getter
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

    @Override
    public String toString() {
        return  "{content=" + content +
                ", user=" + user.getUserName() +
                '}';
    }
}
