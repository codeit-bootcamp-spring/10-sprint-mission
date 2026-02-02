package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import lombok.Locked;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository {
    Optional<ReadStatus> find(UUID id);
    List<ReadStatus> findByUserID(UUID userID);
    List<ReadStatus> findAll();
    ReadStatus save(ReadStatus readStatus);
    void delete(UUID readStatusID);
    void deleteByChannelID(UUID channelID);
}
