package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository {
    Optional<ReadStatus> findByChannelUserId(UUID channelId, UUID userId);
    List<ReadStatus> findAll();
    void save(ReadStatus readStatus);
    void delete(UUID id);
}
