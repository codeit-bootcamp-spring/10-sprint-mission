package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Channel extends Commonness {
    private String name;
    private List<User> userList;

    public Channel(String name) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.name = name;
        this.userList = new ArrayList<User>();
    }

    public UUID getId() {
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public String getName() {
        return name;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void updateUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void updateName(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return "{" + name +
            userList.stream()
                .map(User::getNickName)
                .toList() +
            "}";

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Channel channel)) return false;
        return Objects.equals(id, channel.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
