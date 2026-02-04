package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponse;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatusResponse create(UserStatusCreateRequest request);
    UserStatusResponse find(UUID id);
    List<UserStatusResponse> findAll();
    // PK 기준 수정
    UserStatusResponse update(UUID id, UserStatusUpdateRequest request);
    // FK 기준 수정
    UserStatusResponse updateByUserId(UUID userId, UserStatusUpdateRequest request);
    void delete(UUID id);
}