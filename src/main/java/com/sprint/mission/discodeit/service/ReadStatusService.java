package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.readStatus.ReadStatusCreateDto;
import com.sprint.mission.discodeit.dto.readStatus.ReadStatusResponseDto;
import com.sprint.mission.discodeit.dto.readStatus.ReadStatusUpdateDto;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    ReadStatusResponseDto create(ReadStatusCreateDto dto);
    ReadStatusResponseDto findReadStatus(UUID channelId, UUID userId);
    List<ReadStatusResponseDto> findAll();
    ReadStatusResponseDto update(UUID channelId, UUID userId);
    void delete(UUID id);
}
