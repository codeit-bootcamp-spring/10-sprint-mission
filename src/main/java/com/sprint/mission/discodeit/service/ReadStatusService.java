package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    ReadStatusDto create(UUID channelId, UUID userId);
    ReadStatusDto find(UUID readStatusId);
    List<ReadStatusDto> findAllByUserId(UUID userId);
    ReadStatusDto update(UUID channelId, UUID userId);
    void delete(UUID readStatusId);
}
