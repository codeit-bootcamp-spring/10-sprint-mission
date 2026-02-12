package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ReadStatusDto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    ReadStatusDto.ReadStatusResponse create(UUID userId, UUID channelId);
    ReadStatusDto.ReadStatusResponse findById(UUID readStatusId);
    List<ReadStatusDto.ReadStatusResponse> findAllByUserId(UUID userId);
    ReadStatusDto.ReadStatusResponse update(UUID readStatusId, Instant lastReadTime);
    void delete(UUID readStatusId);
}
