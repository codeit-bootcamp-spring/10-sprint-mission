package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReadStatusRepository {
    void save(ReadStatus readStatus);
    void deleteByChannelId(UUID channelId);
    void deleteByUserId(UUID userId);
    boolean existsByUserIdAndChannelId(UUID userId, UUID channelId);
    Optional<ReadStatus> findById(UUID id);
    List<ReadStatus> findAllByUserId(UUID userId);
    void delete(UUID id);
}
