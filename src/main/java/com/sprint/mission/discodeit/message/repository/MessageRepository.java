package com.sprint.mission.discodeit.message.repository;

import com.sprint.mission.discodeit.message.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {
    Optional<Message> findById(UUID messageId);
    List<Message> findAll();
    List<Message> findAllByUserId(UUID userId);
    List<Message> findAllByChannelId(UUID channelId);
    void save(Message message);
    void deleteById(UUID messageId);
}
