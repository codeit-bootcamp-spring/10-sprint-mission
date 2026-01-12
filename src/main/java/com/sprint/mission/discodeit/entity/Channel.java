package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Channel extends BaseEntity {
    // enum
    private final List<Message> messages = new ArrayList<>();

    // field
    private final List<UUID> userIds = new ArrayList<>(); // 채널에 참여한 유저 목록
    private String name;
    private String description;

    // constructor
    public Channel(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getter, update
    public List<UUID> getUserIds() {return List.copyOf(this.userIds);}
    public String getName() {return name;}
    public String getDescription() {return description;}

    public void update(String name, String description){
        updateTimestamp();
        this.name = name;
        this.description = description;
    }

    // userId
    public void addUser(UUID userId){
        if (userId == null)
            return;
        if (!this.userIds.contains(userId))
            this.userIds.add(userId);
    }

    public void removeUser(UUID userId) {
            userIds.remove(userId);
    }

    public void addMessage(Message message) {
        this.messages.add(message);
        this.updateTimestamp(); // 채널에 변화가 생겼으므로 업데이트 시간 갱신
    }

    public void removeMessage(UUID messageId) {
        this.messages.removeIf(m -> m.getId().equals(messageId));
        this.updateTimestamp();
    }

    public List<Message> getMessages() {
        return Collections.unmodifiableList(messages);
    }
}
