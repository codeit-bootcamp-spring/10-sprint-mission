package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserStatusResponse;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UUID create(UUID userId);
    UserStatusResponse find(UUID id);
    List<UserStatusResponse> findAll();
    UserStatusResponse update(UUID id);
    UserStatusResponse updateByUserId(UUID userId);
    void delete(UUID id);
    void deleteByUserId(UUID userId);
}
