package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository extends DomainRepository<UserStatus> {
    Optional<UserStatus> findByUserId(UUID userId) throws IOException;
    boolean existsByUserId(UUID userId) throws IOException;
    List<UserStatus> findAll() throws IOException;
}
