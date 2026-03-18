package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository extends JpaRepository<UserStatus, UUID> {
    @Query("SELECT us FROM UserStatus us WHERE us.user.id = :userId")
    Optional<UserStatus> findByUserId(UUID userId);
    void deleteByUserId(UUID userId);
}
