package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;

public class Channel extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private IsPrivate isPrivate;
    private User owner; // 채널 주인

    public Channel(String name, IsPrivate isPrivate, User owner) {
        super(UUID.randomUUID(), System.currentTimeMillis());
        this.name = name;
        this.isPrivate = isPrivate;
        addOwner(owner);
    }

    public void addOwner(User owner){
        this.owner = owner;
        if (!owner.getChannels().contains(this)) {
            owner.addChannel(this);
        }
    }

    public User getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public IsPrivate getIsPrivate() {
        return isPrivate;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updatePrivate(IsPrivate isPrivate) {
        this.isPrivate = isPrivate;
    }

    @Override
    public String toString() {
        return "채널명 : " + name + ", 공개여부 : " + isPrivate + ", 채널장 : " + owner.getName();
    }
}
