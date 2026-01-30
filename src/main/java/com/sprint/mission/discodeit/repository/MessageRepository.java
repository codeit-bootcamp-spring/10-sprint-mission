package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {
    Message save(Message message);

    Optional<Message> findById(UUID id);

    List<Message> findAll();

    void deleteById(UUID id);

    Optional<Instant> findLatestCreatedAtByChannelId(UUID channelId);

    void deleteAllByChannelId(UUID channelId);
}
