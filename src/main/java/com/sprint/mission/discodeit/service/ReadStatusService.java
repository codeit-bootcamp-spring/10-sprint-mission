package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.readstatusdto.ReadStatusCreateRequestDTO;
import com.sprint.mission.discodeit.dto.readstatusdto.ReadStatusResponseDTO;
import com.sprint.mission.discodeit.dto.readstatusdto.ReadStatusUpdateRequestDTO;

import com.sprint.mission.discodeit.dto.userdto.UserCreateRequestDTO;
import java.util.List;
import java.util.UUID;

public interface ReadStatusService {

    //ReadStatusResponseDTO create(ReadStatusCreateRequestDTO req);
    ReadStatusResponseDTO create(ReadStatusCreateRequestDTO req);

    ReadStatusResponseDTO find(UUID rsId);

    List<ReadStatusResponseDTO> findAllByUserId(UUID userId);

    ReadStatusResponseDTO update(UUID readStatusId, ReadStatusUpdateRequestDTO req);

    void delete(UUID id);


}
