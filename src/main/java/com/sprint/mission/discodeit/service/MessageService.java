package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message createMessage (UUID userId, UUID channelId, String content);
    Message findMessageById(UUID messageId);
    Message updateMessage (UUID messageId, String content);
    List<Message> findMessageByUserId(UUID userId);
    List<Message> findMessageByChannelId(UUID channelId);
    void deleteMessage(UUID messageId);
}
