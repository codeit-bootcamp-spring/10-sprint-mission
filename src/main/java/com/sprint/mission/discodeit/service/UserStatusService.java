package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserStatusDto;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatusDto.Response create(UserStatusDto.Create createRequest);
    UserStatusDto.Response findById(UUID userStatusId);
    List<UserStatusDto.Response> findAll();
    UserStatusDto.Response update(UserStatusDto.Update updateRequest);
    UserStatusDto.Response updateByUserId(UserStatusDto.Update updateRequest);
    void delete(UUID userStatusId);
}
