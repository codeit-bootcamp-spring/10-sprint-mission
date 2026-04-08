package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.readstatus.CreateReadStatusRequestDTO;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusDto;
import com.sprint.mission.discodeit.dto.readstatus.UpdateReadStatusRequestDTO;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    ReadStatusDto createReadStatus(CreateReadStatusRequestDTO dto);

    ReadStatusDto findById(UUID statusId);

    List<ReadStatusDto> findAllByUserId(UUID userId);

    ReadStatusDto updateReadStatus(UUID statusId, UpdateReadStatusRequestDTO dto);

    void deleteById(UUID statusId);
}
