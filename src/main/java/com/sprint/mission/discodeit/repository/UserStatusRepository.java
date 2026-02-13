package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository {
    public UserStatus save(UserStatus userStatus);
    public void deleteById(UUID id);
    public void deleteByUserId(UUID userID);
    public Optional<UserStatus> findById(UUID id);
    public Optional<UserStatus> findByUserId(UUID id);
    public List<UserStatus> findAll();
}
