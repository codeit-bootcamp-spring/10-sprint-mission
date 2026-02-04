package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ReadStatusDto;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    ReadStatusDto.Response create(ReadStatusDto.Create createRequest);
    ReadStatusDto.Response findById(UUID statusId);
    List<ReadStatusDto.Response> findAllByUserId(UUID userId);
    ReadStatusDto.Response update(ReadStatusDto.Update updateRequest);
    void delete(UUID statusId);
}
