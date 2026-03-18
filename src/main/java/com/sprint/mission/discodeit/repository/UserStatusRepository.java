package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserStatusRepository extends JpaRepository<UserStatus, UUID> {

  @EntityGraph(attributePaths = {"user"})
  Optional<UserStatus> findDetailById(UUID userStatusId);

  @EntityGraph(attributePaths = {"user"})
  Optional<UserStatus> findByUserId(UUID userId);

  @EntityGraph(attributePaths = {"user"})
  @Query("SELECT us FROM UserStatus us")
  List<UserStatus> findAllWithUser();

  void deleteByUserId(UUID userId);
}