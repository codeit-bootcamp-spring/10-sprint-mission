package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository {
    ReadStatus save(ReadStatus readStatus);
    List<ReadStatus> findAllByUserId(UUID userId);
    List<ReadStatus> findAllByChannelId(UUID channelId);
    Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId);
    Optional<ReadStatus> find(UUID readStatusId);
    void deleteByChannelId(UUID channelId);
    void deleteByUserId(UUID userId);
    void delete(UUID readStatusId);
}
