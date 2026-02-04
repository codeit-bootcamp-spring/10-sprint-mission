package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {
    Message save(Message message);
    Optional<Message> findById(UUID id);
    void delete(UUID id);
    void clear();
    List<Message> findAllByUserId(UUID userId);
    List<Message> findAllByChannelId(UUID channelId);
    void deleteByChannelId(UUID channelId);
    void deleteByUserId(UUID userId);
}
