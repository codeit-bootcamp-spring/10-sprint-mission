package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserStatusRepository extends JpaRepository<UserStatus, UUID> {

    @Query("select us from UserStatus us where us.user.id = :userId")
    Optional<UserStatus> findByUserId(UUID userId);

    @Query("select (count(us) > 0) from UserStatus us where us.user.id = :userId")
    boolean existsByUserId(UUID userId);
}
