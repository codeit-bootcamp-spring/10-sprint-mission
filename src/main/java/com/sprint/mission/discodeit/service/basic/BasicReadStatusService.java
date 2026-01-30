package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.readStatus.ReadStatusCreateRequestDTO;
import com.sprint.mission.discodeit.dto.request.readStatus.ReadStatusUpdateRequestDTO;
import com.sprint.mission.discodeit.dto.response.ReadStatusResponseDTO;
import com.sprint.mission.discodeit.service.ReadStatusService;

import java.util.List;
import java.util.UUID;

public class BasicReadStatusService implements ReadStatusService {
    @Override
    public ReadStatusResponseDTO create(ReadStatusCreateRequestDTO readStatusCreateRequestDTO) {
        return null;
    }

    @Override
    public ReadStatusResponseDTO findById(UUID id) {
        return null;
    }

    @Override
    public List<ReadStatusResponseDTO> findAll() {
        return List.of();
    }

    @Override
    public ReadStatusResponseDTO update(ReadStatusUpdateRequestDTO readStatusUpdateRequestDTO) {
        return null;
    }

    @Override
    public void delete(UUID id) {

    }
}
