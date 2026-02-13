package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.readStatus.ReadStatusCreateDto;
import com.sprint.mission.discodeit.dto.readStatus.ReadStatusResponseDto;
import com.sprint.mission.discodeit.dto.readStatus.ReadStatusUpdateDto;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    ReadStatusResponseDto create(ReadStatusCreateDto dto);
    ReadStatusResponseDto findReadStatus(UUID id);
    List<ReadStatusResponseDto> findAllByUserIdChannelId(UUID userId, UUID channelId);
    ReadStatusResponseDto update(UUID id);
    void delete(UUID id);
}
