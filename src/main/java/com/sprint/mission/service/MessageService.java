package com.sprint.mission.service;

import com.sprint.mission.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message create(UUID userId, UUID channelId, String content);

//    Message findById(UUID id);

    List<Message> getAllMessage();

    Message update(UUID userId, UUID messageId, String content);

    void deleteMessage(UUID userId, UUID messageId);

//    List<Message> findByChannelId(UUID channelId);

//    List<Message> findByUserIdAndChannelId(UUID userId, UUID channelId);

    Message getMessageOrThrow(UUID messageId);

    List<Message> getMessagesInChannel(UUID channelId);

    List<Message> getMessagesByUserInChannel(UUID userId, UUID channelId);
}
