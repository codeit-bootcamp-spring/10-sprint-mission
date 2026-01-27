package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message create(String content, UUID channelId, UUID userId);
    List<Message> findMessagesByUserAndChannel(UUID channelId, UUID userId);
    List<Message> findMessagesByChannel(UUID channelId);
    List<Message> findMessagesByUser(UUID userId);
    List<Message> findAllMessages();
    Message findMessageById(UUID messageId);
    Message update(UUID messageId, String content);
    void delete(UUID messageId);
}
