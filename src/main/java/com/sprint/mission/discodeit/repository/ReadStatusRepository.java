package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository {

    void save(ReadStatus readStatus);

    List<ReadStatus> findAllByChannelId(UUID channelId);

    void delete(ReadStatus readStatus);

    boolean existsByChannelId(UUID channelId);

    boolean existsByUserId(UUID userId);

    Optional<ReadStatus> findById(UUID readStatusId);

    List<ReadStatus> findAllByUserId(UUID userId);

    void deleteById(UUID readStatusId);
}
