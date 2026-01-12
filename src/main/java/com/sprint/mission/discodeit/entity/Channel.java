package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;

public class Channel extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private IsPrivate isPrivate;

    public Channel(String name, IsPrivate isPrivate) {
        super(UUID.randomUUID(), System.currentTimeMillis());
        this.name = name;
        this.isPrivate = isPrivate;
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
        isPrivate = isPrivate;
    }

    @Override
    public String toString() {
        return "채널명 : " + name + ", 공개여부 : " + isPrivate;
    }
}
