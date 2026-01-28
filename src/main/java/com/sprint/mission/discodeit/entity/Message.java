package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;

public class Message extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private final UUID channelId;
    private final UUID userId;
    private String content;


    public Message (UUID userId, UUID channelId, String content) {
        super();
        this.userId = userId;
        this.channelId = channelId;
        this.content = content;
    }

    public void setContent(String setMessage) {
        content = setMessage;
        setUpdatedAt(System.currentTimeMillis());
    }

    public String getContent() {
        return content;
    }

    public UUID getChannelId() { return channelId;}
    public UUID getUserId() { return userId; }

}
