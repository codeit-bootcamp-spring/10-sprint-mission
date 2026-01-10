package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageService {
    void sendMessage(UUID userId, String content);

    List<Message> getAllMessages();

    List<Message> getMessageListByUser(UUID userId);

    Optional<Message> getMessageById(UUID messageId);

    void editMessage(UUID messageId, String newContent);

    void deleteMessage(UUID messageId);
}
