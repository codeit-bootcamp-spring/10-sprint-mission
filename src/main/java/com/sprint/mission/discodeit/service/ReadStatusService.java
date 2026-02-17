package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.readstatusdto.ReadStatusCreateRequestDTO;
import com.sprint.mission.discodeit.dto.readstatusdto.ReadStatusResponseDTO;
import com.sprint.mission.discodeit.dto.readstatusdto.ReadStatusUpdateRequestDTO;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    //ReadStatusResponseDTO create(ReadStatusCreateRequestDTO req);
    List<ReadStatusResponseDTO> create(UUID channelId);
    ReadStatusResponseDTO find(UUID rsId);
    List<ReadStatusResponseDTO> findAllByUserId(UUID userId);
    ReadStatusResponseDTO update(ReadStatusUpdateRequestDTO req);
    void delete(UUID id);



}
