package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.UUID;
import java.util.List;

public class Channel {
    private final UUID id;
    private String name;
    private String description;
    private final List<User> members = new ArrayList<>();
    private final Long createdAt;
    private Long updatedAt;

    public Channel (String name, String description) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
        this.name = name;
        this.description = description;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Long getCreatedAt() { return createdAt; }
    public Long getUpdatedAt() { return updatedAt; }

    public void update(String name, String description) {
        this.name = name;
        this.description = description;
        this.updatedAt = System.currentTimeMillis();
    }

    public void addMember(User user){
        if (this.members.contains(user)) {
            throw new IllegalStateException("이미 채널에 가입된 멤버");
        }
        this.members.add(user);
    }

    public boolean isMember(User user){
        return members.contains(user);
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
