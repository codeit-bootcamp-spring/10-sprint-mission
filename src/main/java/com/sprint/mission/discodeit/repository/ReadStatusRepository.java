package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository {
    ReadStatus save(ReadStatus ReadStatus);
    Optional<ReadStatus> findById(UUID id);
    List<ReadStatus> findAll();
    List<ReadStatus> findAllByChannelId(UUID channelId);
    List<ReadStatus> findAllByUserId(UUID userId);
    boolean existsByUserIdAndChannelId(UUID userId, UUID channelId);
    boolean existsById(UUID id);
    void deleteById(UUID id);
    void deleteByChannelId(UUID channelId);
}
