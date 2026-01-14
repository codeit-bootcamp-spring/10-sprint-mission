package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.UUID;

public class User extends BaseDomain {
    // 필드
    private String name;
    private ArrayList<Message> messageList;

    // 생성자
    public User() {
        super();
        this.name = "기본 이름";
    }
    public User(String name) {
        super();
        this.name = name;
    }

    // 메소드
    public UUID getId() {
        return this.id;
    }

    public long getCreatedAt() {
        return this.createdAt;
    }

    public long getUpdatedAt() {
        return this.updatedAt;
    }

    public ArrayList<Message> getMessageList() {
        return this.messageList;
    }

    public void updateName(String name) {
        this.name = name;
        this.updatedAt = System.currentTimeMillis();
    }

    public String toString() {
        return "이 유저의 id: " + this.id + "\n"
                + "이름: " + this.name + "\n"
                + "생성일: " + this.createdAt + "\n"
                + "변경일: " + this.updatedAt;
    }
}
