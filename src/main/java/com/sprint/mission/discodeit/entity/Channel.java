package com.sprint.mission.discodeit.entity;

import java.util.*;

public class Channel extends Entity {
    private String name;
    private User owner;
    private Set<UUID> users;

    public Channel(String name, User owner) {
        super();
        this.name = name;
        this.owner = owner;
        this.users = new HashSet<>();
        join(owner.getId());
    }

    public String getName() {
        return name;
    }

    public User getOwner() {
        return owner;
    }

    public Set<UUID> getUsers() {
        return new HashSet<>(users);
    }

    public Channel update(String name) {
        super.update();
        this.name = name;
        return this;
    }

    public UUID join(UUID userId) {
        users.add(userId);
        return userId;
    }

    @Override
    public String toString() {
        return String.format(
                "Channel [id=%s, name=%s, owner=%s(%s)]",
                getId().toString().substring(0, 5),
                name,
                owner.getNickname(),
                owner.getId().toString().substring(0, 5)
        );
    }
}
