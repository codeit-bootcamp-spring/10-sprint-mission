package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.userStatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.userStatus.UserStatusResponse;
import com.sprint.mission.discodeit.dto.userStatus.UserStatusUpdateRequest;
import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    public UserStatusResponse create(UserStatusCreateRequest request);
    public UserStatusResponse findById(UUID userStatusId);
    public List<UserStatusResponse> findAll();
    public UserStatusResponse update(UserStatusUpdateRequest request);
    public UserStatusResponse updateByUserId(UUID userId);
    public void delete(UUID userStatusId);
}
