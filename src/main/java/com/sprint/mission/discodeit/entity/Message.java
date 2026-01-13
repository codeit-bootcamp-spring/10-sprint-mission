package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message extends BaseEntity {
    // field
    private String content;
    private final UUID userId;
    private final UUID channelId;

    // constructor
    public Message(String content, UUID userId, UUID channelId){
        this.content = content;
        this.userId = userId;
        this.channelId = channelId;
    }

    // Getter, update
    public String getContent() {return content;}
    public UUID getUserId() {return userId;}
    public UUID getChannelId() {return channelId;}

    // 메세지 수정
    public void update(String content){
        updateTimestamp();
        this.content = content;
    }
}
