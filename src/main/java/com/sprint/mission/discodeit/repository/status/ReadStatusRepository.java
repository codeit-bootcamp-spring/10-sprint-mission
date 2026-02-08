package com.sprint.mission.discodeit.repository.status;

import com.sprint.mission.discodeit.entity.status.ReadStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface ReadStatusRepository {
    ReadStatus save(ReadStatus readStatus);

    Optional<ReadStatus> findById(UUID id);

    Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId);

    List<ReadStatus> findByUserId(UUID userId);

    List<ReadStatus> findByChannelId(UUID channelId);

    List<ReadStatus> findAll();

    void deleteById(UUID id);
}
