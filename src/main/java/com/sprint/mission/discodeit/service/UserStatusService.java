package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserStatusDto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatusDto.Response create(UserStatusDto.CreateRequest request);
    UserStatusDto.Response findById(UUID id);
    List<UserStatusDto.Response> findAll();
    UserStatusDto.Response update(UserStatusDto.UpdateRequest request);
    UserStatusDto.Response updateByUserId(UUID userId, Instant lastOnlineAt);
    void delete(UUID id);
}
