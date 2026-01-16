package com.sprint.mission.repository;

import com.sprint.mission.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {
    Message save(Message message);

    Optional<Message> findById(UUID id);

    List<Message> findByChannelId(UUID channelId);

    List<Message> findByUserIdAndChannelId(UUID userId, UUID channelId);

    List<Message> findAll();

    void deleteById(UUID MessageId);
}
