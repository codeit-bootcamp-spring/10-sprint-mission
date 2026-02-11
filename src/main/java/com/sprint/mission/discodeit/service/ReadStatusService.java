package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequestDTO;
import com.sprint.mission.discodeit.dto.response.ReadStatusResponseDTO;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequestDTO;

import java.util.List;
import java.util.UUID;


public interface ReadStatusService {
    ReadStatusResponseDTO create(ReadStatusCreateRequestDTO readStatusCreateRequestDTO);
    ReadStatusResponseDTO find(UUID readStatusId);
    List<ReadStatusResponseDTO> findAllByUserId(UUID userId);
    ReadStatusResponseDTO update(UUID readStatusId, ReadStatusUpdateRequestDTO readStatusUpdateRequestDTO);
    void delete(UUID ReadStatusId);
}
