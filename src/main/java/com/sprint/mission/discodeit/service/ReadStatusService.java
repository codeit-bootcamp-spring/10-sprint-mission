package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ReadStatusCreateDto;
import com.sprint.mission.discodeit.dto.ReadStatusInfoDto;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateDto;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    ReadStatusInfoDto create(ReadStatusCreateDto readStatusCreateDto);
    ReadStatusInfoDto findById(UUID uuid);
    List<ReadStatusInfoDto> findAllByUserId(UUID userId);
    ReadStatusInfoDto update(ReadStatusUpdateDto readStatusUpdateDto);
    void delete(UUID id);
}
