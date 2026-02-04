package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UUID create(UUID userId);
    UserStatus find(UUID id);
    List<UserStatus> findAll();
    UserStatus update(UUID id);
    UserStatus updateByUserId(UUID userId);
    void delete(UUID id);
    void deleteByUserId(UUID userId);
}
