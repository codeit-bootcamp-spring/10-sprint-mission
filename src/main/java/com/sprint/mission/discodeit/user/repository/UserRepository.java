package com.sprint.mission.discodeit.user.repository;

import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.user.entity.constant.UserRole;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, UUID> {

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);

  boolean existsByRole(UserRole role);

  Optional<User> findByUsername(String username);

  @Query("select u.id from User u where u.id in :ids")
  List<UUID> filterExistingIds(@Param("ids") Collection<UUID> ids);

  @EntityGraph(attributePaths = {"profile", "status"})
  Optional<User> findProfileAndStatusById(UUID id);

  @EntityGraph(attributePaths = {"profile", "status"})
  List<User> findAllUsersProfileAndStatusBy();

  @EntityGraph(attributePaths = {"profile", "status"})
  List<User> findProfileAndStatusByIdIn(Collection<UUID> id);
}
