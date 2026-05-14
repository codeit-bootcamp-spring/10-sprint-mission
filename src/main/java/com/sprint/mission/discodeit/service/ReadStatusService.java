package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.readstatusdto.ReadStatusCreateRequestDTO;
import com.sprint.mission.discodeit.dto.readstatusdto.ReadStatusDto;
import com.sprint.mission.discodeit.dto.readstatusdto.ReadStatusUpdateRequestDTO;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {

    //ReadStatusDto create(ReadStatusCreateRequestDTO req);
    ReadStatusDto create(ReadStatusCreateRequestDTO req);

    ReadStatusDto find(UUID rsId);

    List<ReadStatusDto> findAllByUserId(UUID userId);

    ReadStatusDto update(UUID readStatusId, ReadStatusUpdateRequestDTO req);

    void delete(UUID id);


}
