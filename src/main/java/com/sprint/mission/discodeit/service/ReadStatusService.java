package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ReadStatusDto;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    ReadStatusDto.Response create(ReadStatusDto.CreateRequest request);
    ReadStatusDto.Response find(UUID readStatusId);
    List<ReadStatusDto.Response> findAllByUserId(UUID channelId);
    ReadStatusDto.Response update(UUID readStatusId, ReadStatusDto.UpdateRequest request);
    void delete(UUID readStatusId);
}

