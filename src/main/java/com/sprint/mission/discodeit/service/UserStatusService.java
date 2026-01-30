package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserStatusDTO;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatusDTO.Response create(UserStatusDTO.Create createRequest);
    UserStatusDTO.Response findById(UUID userStatusId);
    List<UserStatusDTO.Response> findAll();
    UserStatusDTO.Response update(UserStatusDTO.Update updateRequest);
    UserStatusDTO.Response updateByUserId(UserStatusDTO.Update updateRequest);
    void delete(UUID userStatusId);
}
