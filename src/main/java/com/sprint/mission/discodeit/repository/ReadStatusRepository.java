package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository {
    ReadStatus save(ReadStatus readStatus);
    Optional<ReadStatus> findById(UUID uuid);
    List<ReadStatus> findAll();
    List<ReadStatus> findAllByUserId(UUID userId);
    void deleteById(UUID uuid);
    void deleteAllByChannelId(UUID channelId);
}
