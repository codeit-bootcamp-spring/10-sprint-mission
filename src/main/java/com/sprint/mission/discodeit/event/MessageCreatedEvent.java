package com.sprint.mission.discodeit.event;

import lombok.Getter;

import java.util.UUID;

@Getter
public class MessageCreatedEvent {
    private final UUID messageId;
    private final UUID channelId;
    private final String channelName;
    private final UUID senderId;
    private final String senderName;
    private final String content;

    public MessageCreatedEvent(UUID messageId, UUID channelId, String channelName,UUID senderId, String senderName, String content){
        this.messageId = messageId;
        this.channelId = channelId;
        this.channelName = channelName;
        this.senderId = senderId;
        this.senderName = senderName;
        this.content = content;
    }
}
