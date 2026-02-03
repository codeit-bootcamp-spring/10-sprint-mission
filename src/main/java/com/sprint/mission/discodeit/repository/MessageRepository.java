package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {

    void save(Message message);

    Optional<Message> findById(UUID id);

    List<Message> findAll();

    void delete(Message message);

    List<Message> findByChannelId(UUID channelId);

    Instant findLastMessageAtByChannelId(UUID channelId);

    Map<UUID, Instant> findLastMessageAtByChannelIds(List<UUID> channelIds);

    void deleteById(UUID messageId);
}
