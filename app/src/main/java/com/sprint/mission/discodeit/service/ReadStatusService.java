package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ReadStatusDto;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    ReadStatusDto createReadStatus(ReadStatusDto.ReadStatusCreateRequest createReq);
    ReadStatusDto findById(UUID uuid);
    List<ReadStatusDto> findAllByUserId(UUID userId);
    ReadStatusDto updateReadStatus(UUID uuid, ReadStatusDto.ReadStatusUpdateRequest updateReq);
    void deleteReadStatusById(UUID uuid);
}
