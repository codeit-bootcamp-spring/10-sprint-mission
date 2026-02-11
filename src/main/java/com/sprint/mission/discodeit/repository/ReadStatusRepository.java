package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReadStatusRepository {
    Optional<ReadStatus> findById(UUID id);
    Optional<ReadStatus> findByUserAndChannelId(UUID userId, UUID channelId);
    List<ReadStatus> findAll();
    void save(ReadStatus ReadStatus);
    void deleteById(UUID id);
}
