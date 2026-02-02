package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message createMessage(UUID channelId, UUID userId, String message);
    Message findMessage(UUID uuid);
    List<Message> findAllByChannelId(UUID channelId);
    List<Message> findAllMessages();
    Message updateMessage(UUID uuid, String newMessage);
    Message updateMessage(Message newMessage);      // File쪽 특화용(다른 JCF관련 필드)
    void deleteMessage(UUID uuid);
}
