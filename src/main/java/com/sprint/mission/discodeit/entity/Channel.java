package com.sprint.mission.discodeit.entity;

public class Channel extends BaseEntity {
    private String name;

    public Channel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                '}';
    }

    public void updateName(String name) {
        this.name = name;
        this.updatedAt = System.currentTimeMillis();
    }
}
