package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequestDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponseDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequestDto;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    public ReadStatusResponseDto create(ReadStatusCreateRequestDto readStatusCreateRequestDto);
    public ReadStatusResponseDto find(UUID id);
    public List<ReadStatusResponseDto> findAllByUserId(UUID userId);
    public ReadStatusResponseDto update(ReadStatusUpdateRequestDto readStatusUpdateRequestDto);
    public void delete(UUID id);
}
