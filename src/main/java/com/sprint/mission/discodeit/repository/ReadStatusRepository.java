package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.io.IOException;
import java.util.UUID;

public interface ReadStatusRepository extends DomainRepository<ReadStatus> {
    boolean existsByUserAndChannelId(UUID userId, UUID channelId) throws IOException;
}
