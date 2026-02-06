package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ReadStatusServiceDTO.ReadStatusCreation;
import com.sprint.mission.discodeit.dto.ReadStatusServiceDTO.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.ReadStatusServiceDTO.ReadStatusUpdate;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService extends DomainService<ReadStatusResponse, ReadStatusCreation, ReadStatusUpdate> {
    List<ReadStatusResponse> findAllByUserId(UUID userId);
}
