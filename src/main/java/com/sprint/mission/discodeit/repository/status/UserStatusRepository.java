package com.sprint.mission.discodeit.repository.status;

import com.sprint.mission.discodeit.entity.status.UserStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository {
    UserStatus save(UserStatus userStatus);
    Optional<UserStatus> findById(UUID id);
    Optional<UserStatus> findByUserId(UUID userId);
    boolean existsByUserId(UUID userId);
    List<UserStatus> findAll();
    void deleteById(UUID id);
    void deleteByUserId(UUID userId);
}
