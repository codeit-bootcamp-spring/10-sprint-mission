package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository {
    public UserStatus save(UserStatus userStatus);
    public UserStatus deleteById(UUID id);
    public UserStatus deleteByUserId(UUID userID);
    public Optional<UserStatus> findById(UUID id);
    public List<UserStatus> findAll();
}
