package com.sprint.mission.discodeit.service.status;

import com.sprint.mission.discodeit.dto.userstatus.CreateUserStatusRequest;
import com.sprint.mission.discodeit.dto.userstatus.UpdateUserStatusRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponse;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {

    UserStatusResponse create(CreateUserStatusRequest request);

    UserStatusResponse find(UUID id);

    List<UserStatusResponse> findAll();

    UserStatusResponse update(UpdateUserStatusRequest request);

    UserStatusResponse updateByUserId(UUID userId, UpdateUserStatusRequest request);

    void delete(UUID id);

}
