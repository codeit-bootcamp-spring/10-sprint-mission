package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.UUID;

public interface ReadStatusRepository {

    void save(ReadStatus readStatus);

    void delete(UUID id);

    void deleteByChannelId(UUID channelId);

    List<ReadStatus> loadAll();

    ReadStatus loadById(UUID id);
}
