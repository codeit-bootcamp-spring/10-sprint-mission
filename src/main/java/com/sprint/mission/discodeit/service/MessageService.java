package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageService {
    Message createMessage(UUID channelId, UUID userId, String message);
    Optional<Message> findMessage(UUID uuid);
    List<Message> findMessageByChannelId(UUID uuid);
    List<Message> findAllMessages();
    Message updateMessage(UUID uuid, String newMessage);
    void deleteMessage(UUID uuid);
}
