package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.UUID;

public interface ReadStatusRepository {
    ReadStatus find(UUID id);
    ReadStatus findAll();
    void save(ReadStatus readStatus);
    void delete(ReadStatus readStatus);
    void deleteByChannelID(UUID channelID);
}
