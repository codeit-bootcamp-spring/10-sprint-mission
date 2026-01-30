package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserStatusRepository {
    void save(UserStatus userStatus);
    Optional<UserStatus> findByUserId(UUID id);
    Map<UUID, UserStatus> findAll();
    void deleteByUserId(UUID userId);
}
