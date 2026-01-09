package org.example.entity;

public class Channel extends BaseEntity {
    private String name;
    private String description;
    private ChannelType type;

    public Channel(String name, String description, ChannelType type) {
        super();
        this.name = name;
        this.description = description;
        this.type = type;
    }

    public void update(String name, String description, ChannelType type) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.updatedAt = System.currentTimeMillis();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ChannelType getType() {
        return type;
    }
}
