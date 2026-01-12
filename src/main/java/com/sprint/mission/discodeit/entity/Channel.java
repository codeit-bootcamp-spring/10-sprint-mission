package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Channel extends Base {
    private String name;
    private List<User> userList;

    public Channel(String name) {
        this.name = name;
        this.userList = new ArrayList<User>();
    }

    public String getName() {
        return name;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void addUser(User user) {
        this.userList.add(user);
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
