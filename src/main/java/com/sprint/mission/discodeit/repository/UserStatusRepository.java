package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserStatusRepository {
    void save(UUID id, UserStatus userStatus);
    Optional<UserStatus> findById(UUID id);
    void deleteByUserId(UUID userId);
}
