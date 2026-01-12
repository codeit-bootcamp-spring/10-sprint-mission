package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message {
    private final UUID id;
    private final UUID channelId;
    private final Long createdAt;
    private Long updatedAt;
    private String sender;
    private String text;

    public Message(Channel channel, String sender, String text) {
        this.id = UUID.randomUUID();
        this.channelId = channel.getId();
        this.sender = sender;
        this.text = text;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
    }

    // Getter 메소드
    public UUID getId() { return id; }
    public UUID getChannelId() { return channelId; }
    public Long getCreatedAt() { return createdAt; }
    public Long getUpdatedAt() { return updatedAt; }
    public String getSender() { return sender; }
    public String getText() { return text; }


    // update 메소드
    public void update(String text) {
        this.text = text;
        this.updatedAt = System.currentTimeMillis();
    }

}
