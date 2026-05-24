package com.sprint.mission.discodeit.userstatus.repository;

import com.sprint.mission.discodeit.userstatus.entity.UserStatus;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStatusRepository extends JpaRepository<UserStatus, UUID> {

  Optional<UserStatus> findByUserId(UUID userId);

  boolean existsByUserId(UUID userId);
}
