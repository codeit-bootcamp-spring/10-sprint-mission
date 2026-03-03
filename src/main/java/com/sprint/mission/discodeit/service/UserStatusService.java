package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.userstatus.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusServiceDTO.UserStatusResponse;
import com.sprint.mission.discodeit.dto.userstatus.command.UserStatusUpdateCommand;

import java.util.List;

public interface UserStatusService extends DomainService<UserStatusResponse, UserStatusCreateRequest, UserStatusUpdateCommand> {
    List<UserStatusResponse> findAll();
}
