package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStatusRepository extends JpaRepository<UserStatus, UUID> {

  UserStatus findByUser_Id(UUID userId);

  void deleteByUser_Id(UUID userId);

  default UserStatus findByUserId(UUID userId) {
    return findByUser_Id(userId);
  }

  default void deleteByUserId(UUID userId) {
    deleteByUser_Id(userId);
  }
}