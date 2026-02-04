package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import lombok.Locked;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusService {
    ReadStatusDto.Response create(ReadStatusDto.CreateRequest request);
    ReadStatusDto.Response find(UUID id);
    List<ReadStatusDto.Response> findAllByUserId(UUID userId);
    ReadStatusDto.Response update(ReadStatusDto.UpdateRequest request);
    void delete(UUID id);
}
