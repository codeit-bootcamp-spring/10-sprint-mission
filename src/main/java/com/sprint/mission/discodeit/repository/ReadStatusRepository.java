package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import lombok.Locked;

import java.util.List;
import java.util.UUID;

public interface ReadStatusRepository {
    public ReadStatus save(ReadStatus readStatus);
    public ReadStatus deleteByID(UUID id);
    public ReadStatus findByID(UUID id);
    public List<ReadStatus> findAll();
}
