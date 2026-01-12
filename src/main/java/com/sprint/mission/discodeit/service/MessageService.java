package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message createMessage(UUID channelId, UUID userId, String msg);
    List<Message> readMessages(UUID channelId);
    Message updateMessageContent(UUID channelId, UUID userId, UUID messageId, String newMessage);
    void deleteMessage(UUID channelId, UUID userId, UUID messageId);
}
