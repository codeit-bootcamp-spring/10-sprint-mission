package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.UUID;

public class Channel extends BaseDomain{
    // 필드
    private String channelName;
    private ArrayList<User> userList;

    // 생성자
    public Channel() {
        super();
        this.channelName = "기본 이름";
        this.userList = new ArrayList<>();
    }
    public Channel(String channelName) {
        super();
        this.channelName = channelName;
        this.userList = new ArrayList<>();
    }

    // 메소드
    public UUID getId() {
        return id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void updateChannelName(String name) {
        this.channelName = name;
        this.updatedAt = System.currentTimeMillis();
    }

    public ArrayList<User> getUserList() {
        return this.userList;
    }

    public String toString() {
        return "이 채널의 id: " + this.id + "\n"
                + "채널명: " + this.channelName + "\n"
                + "생성일: " + this.createdAt + "\n"
                + "변경일: " + this.updatedAt;
    }
}
