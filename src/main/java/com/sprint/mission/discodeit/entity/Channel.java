package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Channel extends BaseDomain implements Serializable {
    // 필드
    private String channelName;
    private String description;
//    private List<User> userList;
    private List<UUID> userList;
//    private List<Message> messagesList;
    private List<UUID> messagesList;
    private boolean isPrivate;

    private static final long serialVersionUID = 1L;

    // 생성자
    public Channel() {
        super();
        this.channelName = "private";
        this.description = "";
        this.userList = new ArrayList<>();
        this.messagesList = new ArrayList<>();
        isPrivate = true;
    }
    public Channel(String channelName) {
        super();
        this.channelName = channelName;
        this.description = "";
        this.userList = new ArrayList<>();
        this.messagesList = new ArrayList<>();
        isPrivate = false;
    }
    public Channel(String channelName, String description) {
        super();
        this.channelName = channelName;
        this.description = description;
        this.userList = new ArrayList<>();
        this.messagesList = new ArrayList<>();
        isPrivate = false;
    }
    // public
    public Channel(String channelName, String description, List<UUID> userList, List<UUID> messagesList) {
        super();
        this.channelName = channelName;
        this.description = description;
        this.userList = userList;
        this.messagesList = messagesList;
        isPrivate = false;
    }
    // private
    public Channel(List<UUID> userList, List<UUID> messagesList) {
        super();
        this.channelName = "private";
        this.description = "";
        this.userList = userList;
        this.messagesList = messagesList;
        isPrivate = true;
    }

    public void updateChannelName(String name) {
        this.channelName = name;
        this.updatedAt = Instant.now();
    }

    public void updateChannelDescription(String description) {
        this.description = description;
        this.updatedAt = Instant.now();
    }


    public String toString() {
        return "이 채널의 id: " + this.id + "\n"
                + "채널명: " + this.channelName + "\n"
                + "생성일: " + this.createdAt + "\n"
                + "변경일: " + this.updatedAt;
    }
}
