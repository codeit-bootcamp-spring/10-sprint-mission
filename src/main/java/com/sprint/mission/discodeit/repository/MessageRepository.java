package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {
    Message save(Message message);
    Optional<Message> findById(UUID uuid);
    List<Message> findAll();
    List<Message> findAllByChannelId(UUID channelId);
    void deleteById(UUID uuid);
    void deleteAllByChannelId(UUID channelId);
}
