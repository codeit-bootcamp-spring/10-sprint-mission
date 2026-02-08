package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface ReadStatusRepository extends DomainRepository<ReadStatus> {
    List<ReadStatus> findAllByChannelId(UUID channelId) throws IOException;
    List<ReadStatus> findAllByUserId(UUID userId) throws IOException;
    boolean existsByUserAndChannelId(UUID userId, UUID channelId) throws IOException;
}
