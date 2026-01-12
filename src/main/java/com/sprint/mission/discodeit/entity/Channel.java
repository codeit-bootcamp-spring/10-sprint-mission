package com.sprint.mission.discodeit.entity;

public class Channel extends BaseEntity{
    private String name;
    private String type;

    public Channel(String name, String type) {
        super();
        this.name = name;
        this.type = type;
    }
    public String getName() {
        return name;
    }
    public String getType() {
        return type;
    }
    public void updateName(String name) {
        this.name = name;
        updateTimestamps();
    }
    public void updateType(String type) {
        this.type = type;
        updateTimestamps();
    }
    @Override
    public String toString() {
        return "채널[이름: " + name +
                ", 타입: " + type + "]";
    }
}
