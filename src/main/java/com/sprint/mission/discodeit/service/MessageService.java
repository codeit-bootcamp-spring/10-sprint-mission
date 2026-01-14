package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageService {
    Message createMessage(String content, UUID channelId, UUID userId);
    void deleteMessage(UUID id);
    Optional<Message> findMessageById(UUID id);
    Message updateMessage(UUID id, String newContent);
    List<Message> findAllMessages();
    List<Message> findMessagesByChannelId(UUID channelId);
    List<Message> findMessagesByUserId(UUID userId);
}
