package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ReadStatusDto;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    ReadStatusDto.Response create(ReadStatusDto.CreateRequest request);
    ReadStatusDto.Response findById(UUID id);
    List<ReadStatusDto.Response> findAllByUserId(UUID userId);
    ReadStatusDto.Response update(ReadStatusDto.UpdateRequest request);
    void delete(UUID id);
}
