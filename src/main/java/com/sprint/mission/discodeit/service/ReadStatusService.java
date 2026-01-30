package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ReadStatusDTO;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    ReadStatusDTO.Response create(ReadStatusDTO.Create createRequest);
    ReadStatusDTO.Response findById(UUID statusId);
    List<ReadStatusDTO.Response> findAllByUserId(UUID userId);
    ReadStatusDTO.Response update(ReadStatusDTO.Update updateRequest);
    void delete(UUID statusId);
}
