package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository {
    Optional<UserStatus> findById(UUID id);
    Optional<UserStatus> findByUserId(UUID userid);
    UserStatus save(UserStatus userStatus);
    List<UserStatus> findAll();
    Map<UUID, UserStatus> getUserStatusMap();
    void delete(UUID id);
    void deleteByUserId(UUID userId);

}
