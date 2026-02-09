package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserStatusServiceDTO.UserStatusCreation;
import com.sprint.mission.discodeit.dto.UserStatusServiceDTO.UserStatusResponse;
import com.sprint.mission.discodeit.dto.UserStatusServiceDTO.UserStatusUpdate;

import java.io.IOException;
import java.util.List;

public interface UserStatusService extends DomainService<UserStatusResponse, UserStatusCreation, UserStatusUpdate> {
    List<UserStatusResponse> findAll() throws IOException;
    UserStatusResponse updateByUserId(UserStatusUpdate model) throws IOException;
}
