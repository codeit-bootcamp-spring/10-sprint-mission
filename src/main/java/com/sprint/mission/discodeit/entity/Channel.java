package com.sprint.mission.discodeit.entity;

public class Channel extends Entity {
    private String name;
    private User owner;

    public Channel(String name, User owner) {
        super();
        this.name = name;
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public User getOwner() {
        return owner;
    }

    public Channel updateChannelName(String name) {
        // 마지막 수정 시각 갱신
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
                owner.getId().toString().substring(0, 5)
        );
    }
}
