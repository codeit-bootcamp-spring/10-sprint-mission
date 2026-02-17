package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserStatusServiceDTO.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.UserStatusServiceDTO.UserStatusResponse;
import com.sprint.mission.discodeit.dto.UserStatusServiceDTO.UserStatusUpdateRequest;

import java.io.IOException;
import java.util.List;

public interface UserStatusService extends DomainService<UserStatusResponse, UserStatusCreateRequest, UserStatusUpdateRequest> {
    List<UserStatusResponse> findAll() throws IOException;
}
