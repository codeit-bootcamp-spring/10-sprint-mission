package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ReadStatusDto;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    ReadStatusDto.readStatusResponse createReadStatus(ReadStatusDto.readStatusCreateRequest createReq);
    ReadStatusDto.readStatusResponse findById(UUID uuid);
    List<ReadStatusDto.readStatusResponse> findAllByUserId(UUID userId);
    ReadStatusDto.readStatusResponse updateReadStatus(UUID uuid, ReadStatusDto.readStatusUpdateRequest updateReq);
    void deleteReadStatusById(UUID uuid);
}
