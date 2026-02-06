package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.UUID;

public interface ReadStatusRepository extends DomainRepository<ReadStatus> {
    List<ReadStatus> findByChannelId(UUID channelId);
    List<ReadStatus> findByUserId(UUID userId);
    boolean existsByUserAndChannelId(UUID userId, UUID channelId);
}
