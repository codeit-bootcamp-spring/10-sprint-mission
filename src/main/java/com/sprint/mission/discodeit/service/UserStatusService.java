package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserStatusDto;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatusDto.Response create(UserStatusDto.CreateRequest request);
    UserStatusDto.Response find(UUID userStatusId);
    List<UserStatusDto.Response> findAll();
    UserStatusDto.Response update(UUID userStatusId, UserStatusDto.UpdateRequest request);
    UserStatusDto.Response updateByUserId(UUID userId, UserStatusDto.UpdateRequest request);

    void delete(UUID userStatusId);
}
