package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository {
    UserStatus save(UserStatus userStatus);
    Optional<UserStatus> findByUserId(UUID userId);
    Optional<UserStatus> find(UUID id);
    List<UserStatus> findAll();
    boolean existsByUserId(UUID userId);
    boolean existsById(UUID id);
    void deleteByUserId(UUID userId);
    void delete(UUID id);
}
