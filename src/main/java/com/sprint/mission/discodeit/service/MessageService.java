package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageService {
    void sendMessage(UUID userId, UUID channelId, String content);

    List<Message> getAllMessages();

    List<Message> getMessageListByUser(UUID userId);

    List<Message> getMessageListByChannel(UUID channelId);

    Optional<Message> getMessageByMessageId(UUID messageId);

    void editMessage(UUID messageId, String newContent);

    void deleteMessage(UUID messageId);

    void clearMessage(UUID channelId);
}
