package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserStatusServiceDTO.UserStatusCreation;
import com.sprint.mission.discodeit.dto.UserStatusServiceDTO.UserStatusResponse;
import com.sprint.mission.discodeit.dto.UserStatusServiceDTO.UserStatusUpdate;

import java.util.List;

public interface UserStatusService extends DomainService<UserStatusResponse, UserStatusCreation, UserStatusUpdate> {
    List<UserStatusResponse> findAll();
    UserStatusResponse updateByUserId(UserStatusUpdate model);
}
