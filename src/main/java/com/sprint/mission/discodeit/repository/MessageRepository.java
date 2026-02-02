package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {
    Message save(Message message);
    Optional<Message> findById(UUID messageId);
    List<Message> findAll();
    void delete(UUID messageId);

    List<Message> findAllByChannelId(UUID channelId);
    void deleteAllByChannelId(UUID channelId);
    Optional<Instant> findLatestMessageTimeByChannelId(UUID channelId);
}
