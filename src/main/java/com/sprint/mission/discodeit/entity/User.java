package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.util.*;

public class User extends Base  {
    // Getter
    // 필드
    @Getter
    private String name;
    @Getter
    private final List<Channel> channelsList;
    @Getter
    private final List<Message> messageList;
    @Getter
    private String email;
    @Getter
    private String password;
    @Getter
    private UUID profileImageID;

    // 생성자
    public User(String name, String email, String password, UUID profileImageID) {
        super();
        this.name = name;
        this.email = email;
        this.password = password;
        this.profileImageID = profileImageID;
        this.channelsList = new ArrayList<>();
        this.messageList = new ArrayList<Message>();
    }

    // Setter
    public void updateName(String name) {
        this.name = name;
        updateUpdatedAt();
    }
    public void updateEmail(String email) {
        this.email = email;
    }
    public void updatePassword(String password) {
        this.password = password;
    }
    public void updateProfileImageID(UUID profileImageID) {
        this.profileImageID = profileImageID;
    }

    // other
    public void joinChannel(Channel channel) {
        channelsList.add(channel);
        updateUpdatedAt();
    }

    public void leaveChannel(Channel channel){
        channelsList.removeIf(ch -> ch.getId().equals(channel.getId()));
        updateUpdatedAt();
    }

    public void addMessage(Message msg){
        messageList.add(msg);
        updateUpdatedAt();
    }

    public void removeMessage(Message msg){
        messageList.remove(msg);
        updateUpdatedAt();
    }
}

