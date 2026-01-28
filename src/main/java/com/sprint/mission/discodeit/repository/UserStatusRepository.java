package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.UUID;

public interface UserStatusRepository {
    UserStatus findById(UUID userId);
    void findAll();
    void save();
    void delete();
}
