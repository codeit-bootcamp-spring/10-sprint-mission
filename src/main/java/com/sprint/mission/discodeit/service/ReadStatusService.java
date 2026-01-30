package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.readStatus.ReadStatusCreateDto;
import com.sprint.mission.discodeit.dto.readStatus.ReadStatusResponseDto;

public interface ReadStatusService {
    ReadStatusResponseDto create(ReadStatusCreateDto dto);
}
