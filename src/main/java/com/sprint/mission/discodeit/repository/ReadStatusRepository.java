package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository {
    Optional<ReadStatus> findById(UUID id);
    List<ReadStatus> findAll();
    List<ReadStatus> findAllByUserIdChannelId(UUID userId, UUID channelId);
    void save(ReadStatus readStatus);
    void delete(UUID id);
}
