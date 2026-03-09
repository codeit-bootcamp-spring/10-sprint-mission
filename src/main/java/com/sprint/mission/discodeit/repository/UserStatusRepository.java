package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository extends JpaRepository<UserStatus, UUID> {

    @EntityGraph(attributePaths = {"user"})
    Optional<UserStatus> findByUser_Id(UUID userId);

    boolean existsByUser_Id(UUID userId);
}
