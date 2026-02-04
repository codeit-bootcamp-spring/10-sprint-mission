package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class User extends BaseDomain implements Serializable {
    // 필드
    private String name;
    private String email;
    private UUID profileId;
    private String password;
    private List<UUID> messageList;
    private List<UUID> channelList;
//    private List<Message> messageList;
//    private List<Channel> channelList;

    private static final long serialVersionUID = 1L;

    // 생성자
    public User() {
        super();
        this.name = "기본 이름";
        this.messageList = new ArrayList<>();
        this.channelList = new ArrayList<>();
    }
    public User(String name, String email, String password) {
        super();
        this.name = name;
        this.email = email;
        this.password = password;
        this.messageList = new ArrayList<>();
        this.channelList = new ArrayList<>();
    }
    public User(String name, String email, String password, UUID profileId) {
        super();
        this.name = name;
        this.email = email;
        this.password = password;
        this.profileId = profileId;
        this.messageList = new ArrayList<>();
        this.channelList = new ArrayList<>();
    }

    public void updateName(String name) {
        this.name = name;
        this.updatedAt = Instant.now();
    }

    public void updateProfile(UUID profileId) {
        this.profileId = profileId;
        this.updatedAt = Instant.now();
    }

    public String toString() {
        return "이 유저의 id: " + this.id + "\n"
                + "이름: " + this.name + "\n"
                + "생성일: " + this.createdAt + "\n"
                + "변경일: " + this.updatedAt;
    }
}
