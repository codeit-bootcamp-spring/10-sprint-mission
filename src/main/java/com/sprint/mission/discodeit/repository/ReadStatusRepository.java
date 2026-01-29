package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository {
    Optional<ReadStatus> findById(UUID userId, UUID channelId);
    void save(ReadStatus ReadStatus);
    void deleteById(UUID id);
}
