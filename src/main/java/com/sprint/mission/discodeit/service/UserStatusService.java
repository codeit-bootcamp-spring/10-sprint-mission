package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.userStatus.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.userStatus.response.UserStatusResponse;
import com.sprint.mission.discodeit.dto.userStatus.request.UserStatusUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatusResponse create(UserStatusCreateRequest request);
    UserStatusResponse find(UUID userID);
    List<UserStatusResponse> findAll();
    UserStatusResponse update(UserStatusUpdateRequest request);
    UserStatusResponse updateByUserID(UUID userID);
    void delete(UUID userStatusID);

}
