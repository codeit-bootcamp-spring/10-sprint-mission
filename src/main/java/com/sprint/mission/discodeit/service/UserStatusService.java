package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserStatusServiceDTO.UserStatusCreation;
import com.sprint.mission.discodeit.dto.UserStatusServiceDTO.UserStatusResponse;
import com.sprint.mission.discodeit.dto.UserStatusServiceDTO.UserStatusUpdate;

public interface UserStatusService extends DomainService<UserStatusResponse, UserStatusCreation, UserStatusUpdate> {
    UserStatusResponse updateByUserId(UserStatusUpdate model);
}
