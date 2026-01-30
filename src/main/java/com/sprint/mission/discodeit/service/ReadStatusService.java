package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ReadStatusCreateDto;
import com.sprint.mission.discodeit.dto.ReadStatusInfoDto;

public interface ReadStatusService {
    ReadStatusInfoDto create(ReadStatusCreateDto readStatusCreateDto);
}
