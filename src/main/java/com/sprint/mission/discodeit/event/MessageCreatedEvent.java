package com.sprint.mission.discodeit.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MessageCreatedEvent {
    private UUID messageId;
    private UUID channelId;
    private String channelName;
    private UUID senderId;
    private String senderName;
    private String content;
}
