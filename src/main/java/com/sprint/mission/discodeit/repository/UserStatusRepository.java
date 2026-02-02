package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository {
    public Optional<UserStatus> findById(UUID id);
    public Optional<UserStatus> findByUserId(UUID userid);
    public void save(UserStatus userStatus);
    public List<UserStatus> findAll();
    public void delete(UUID id);
    public void deleteByUserId(UUID userId);

}
