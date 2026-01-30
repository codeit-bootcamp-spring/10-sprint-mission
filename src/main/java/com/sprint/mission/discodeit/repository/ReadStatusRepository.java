package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReadStatusRepository {

    ReadStatus save(ReadStatus readStatus);

    Optional<ReadStatus> findById(UUID readStatusId);

    List<ReadStatus> findByUserId(UUID userId);

    List<ReadStatus> findAll();

    boolean existsByUserIdAndChannelId(UUID userId, UUID channelId);

    void deleteById(UUID readStatusId);

    void deleteByChannelId(UUID channelId);
}
