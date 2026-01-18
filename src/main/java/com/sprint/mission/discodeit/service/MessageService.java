package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message create(String text, UUID channelId, UUID userId);
    List<Message> findMessagesByUserAndChannel(UUID channelId, UUID userId);
    List<Message> findMessagesByChannel(UUID channelId);
    List<Message> findMessagesByUser(UUID userId);
    List<Message> findAllMessages();
    Message findMessage(UUID messageId);
    Message update(UUID messageId, String text);
    void saveOrUpdate(Message message);
    void delete(UUID messageId);
}
