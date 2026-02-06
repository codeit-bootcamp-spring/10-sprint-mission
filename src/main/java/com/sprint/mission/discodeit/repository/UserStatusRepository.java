package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository extends DomainRepository<UserStatus> {
    Optional<UserStatus> findByUserId(UUID userId);
    boolean existsByUserId(UUID userId);
}
