package com.sprint.mission.discodeit.event;

import lombok.Getter;

import java.util.UUID;

@Getter
public class MessageCreatedEvent {
    private final UUID messageId;
    private final UUID channelId;
    private final UUID authorId;
    private final String authorUsername;
    private final String channelName;
    private final String content;

    public MessageCreatedEvent(UUID messageId, UUID channelId, UUID authorId,
                               String authorUsername, String channelName, String content) {
        this.messageId = messageId;
        this.channelId = channelId;
        this.authorId = authorId;
        this.authorUsername = authorUsername;
        this.channelName = channelName;
        this.content = content;
    }
}
