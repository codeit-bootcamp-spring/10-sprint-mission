package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.UUID;

public interface ReadStatusRepository {
    ReadStatus save(ReadStatus readStatus);
    ReadStatus findById(UUID readStatusId);
    List<ReadStatus> findAll();
    void delete(ReadStatus readStatus);
}
