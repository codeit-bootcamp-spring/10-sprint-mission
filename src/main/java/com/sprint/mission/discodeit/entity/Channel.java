package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Channel extends Entity {
    private String name;
    private UUID owner;

    public Channel(String name, UUID owner) {
        super();
        this.name = name;
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public UUID getOwner() {
        return owner;
    }

    public Channel updateChannelName(String name) {
        super.update();
        this.name = name;
        return this;
    }

    @Override
    public String toString() {
        return String.format(
                "Channel [id=%s, name=%s, owner=%s]",
                getId().toString().substring(0, 5),
                name,
                owner.toString().substring(0, 5)
        );
    }
}
