package com.sprint.mission.discodeit.user.repository;

import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.user.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JPAUserStatusRepository extends JpaRepository<UserStatus, UUID> {

  Optional<UserStatus> findByUser(User user);

  Optional<UserStatus> findByUserId(UUID userId);


}
