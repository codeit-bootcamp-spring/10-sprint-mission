package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.UUID;

public interface ReadStatusRepository {

    void save(ReadStatus readStatus);

    List<ReadStatus> findByChannelId(UUID channelId);

    void delete(ReadStatus readStatus);
}
