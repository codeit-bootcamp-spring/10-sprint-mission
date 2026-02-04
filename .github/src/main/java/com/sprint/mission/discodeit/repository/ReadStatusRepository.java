package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository {
    ReadStatus save(ReadStatus readStatus);
    Optional<ReadStatus> findById(UUID id);

    Optional<ReadStatus> findByUserAndChannelId(UUID userId, UUID channelId);

    void deleteById(UUID id);
    void deleteAllByChannelId(UUID channelId);
    public List<ReadStatus> findAll();

    List<ReadStatus> findAllByChannelId(UUID channelId);
    List<ReadStatus> findAllByUserId(UUID userId);

    boolean existsByChannelIdAndUserId(UUID channelId, UUID userId);
    boolean existsById(UUID id);
}
