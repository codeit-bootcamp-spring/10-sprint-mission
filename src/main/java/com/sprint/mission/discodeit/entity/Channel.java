package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.*;

public class Channel extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String intro;

    private Set<UUID> userId = new HashSet<>();
    private Set<UUID> messageId = new HashSet<>();


    public Channel(String name, String intro) {
        super();
        this.name = name;
        this.intro = intro;
    }

    public String getName(){
        return name;
    }

    public void setName(String name) {
        this.name = name;
        setUpdatedAt(System.currentTimeMillis());
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro){
        this.intro = intro;
        setUpdatedAt(System.currentTimeMillis());
    }

    public Set<UUID> getUserList() {
        return userId;
    }
    public Set<UUID> getMessageList() { return messageId; }

    public void addUser(UUID userId) {
        this.userId.add(userId);
    }

    public void removeUser(UUID userId) {
        this.userId.remove(userId);
    }

    public void addMessage(UUID messageId) {
        this.messageId.add(messageId);
    }
    public void remove(UUID messageId) {
        this.messageId.remove(messageId);
    }
}
