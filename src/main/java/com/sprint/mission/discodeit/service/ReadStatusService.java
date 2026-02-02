package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.readstatus.CreateReadStatusRequestDTO;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponseDTO;
import com.sprint.mission.discodeit.dto.readstatus.UpdateReadStatusRequestDTO;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    ReadStatusResponseDTO createReadStatus(CreateReadStatusRequestDTO dto);

    ReadStatusResponseDTO findById(UUID statusId);

    List<ReadStatusResponseDTO> findAllByUserId(UUID userId);

    ReadStatusResponseDTO updateReadStatus(UpdateReadStatusRequestDTO dto);

    void deleteById(UUID statusId);
}
