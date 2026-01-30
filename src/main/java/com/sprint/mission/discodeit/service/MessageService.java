package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message createMessage(String content, UUID senderId, UUID channelId);
    Message findMessage(UUID messageId);
    List<Message> findAll();
    List<Message> findAllByUserId(UUID userId);
    List<Message> findAllByChannelId(UUID channelId);
    Message updateMessage(UUID messageId, String content);
    void deleteMessage(UUID messageId);
}
