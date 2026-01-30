package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message createMessage(String content, UUID senderId, UUID channelId);
    Message getMessage(UUID messageId);
    List<Message> getAllMessages();
    List<Message> getMessagesByUserId(UUID userId);
    List<Message> getMessagesByChannelId(UUID channelId);
    Message updateMessage(UUID messageId, String content);
    void deleteMessage(UUID messageId);
}
