package com.sprint.mission.discodeit.userStatus.repository;

import com.sprint.mission.discodeit.userStatus.model.UserStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository {
    UserStatus save(UserStatus userStatus);

    Optional<UserStatus> findById(UUID id);

    Optional<UserStatus> findByUserId(UUID userId);

    List<UserStatus> findAll();

    void deleteById(UUID id);
}
