package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ReadStatus.ReadStatusRequestCreateDto;
import com.sprint.mission.discodeit.dto.ReadStatus.ReadStatusRequestUpdateDto;
import com.sprint.mission.discodeit.dto.ReadStatus.ReadStatusResponseDto;
import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    ReadStatusResponseDto create(ReadStatusRequestCreateDto request);

    ReadStatusResponseDto find(UUID id);

    List<ReadStatusResponseDto> findAllByUserId(UUID id);

    void update(ReadStatusRequestUpdateDto request);

    void delete(UUID id);

}
