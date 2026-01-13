package com.sprint.mission.discodeit.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Groups extends DefaultEntity{
    private String groupName;
    private Set<User> users;

    public Groups(String groupName) {
        this.groupName = groupName;
        this.users = new HashSet<>();
    }

    public String getGroupName() {
        return groupName;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void addUser(User user) {
        users.add(user);
    }

    public void removeUser(UUID id) {
        users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .ifPresent(users::remove);
    }
}
