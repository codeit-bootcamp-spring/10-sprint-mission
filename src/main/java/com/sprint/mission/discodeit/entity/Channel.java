package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Channel extends BaseEntity {
    // enum
    public enum ChannelType {TEXT, VOICE}

    // field
    private String name;
    private String description;
    private ChannelType type;

    // constructor
    public Channel(String name, String discription, ChannelType type) {
        this.name = name;
        this.description = discription;
        this.type = type;
    }

    // Getter, update
    public String getName() {return name;}
    public String getDescription() {return description;}
    public ChannelType getType() {return type;}

    public void update(String name, String discription, ChannelType type){
        updateTimestamp();
        this.name = name;
        this.description = discription;
        this.type = type;
    }
}
