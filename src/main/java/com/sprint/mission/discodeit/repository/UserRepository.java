package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, UUID> {

  @EntityGraph(attributePaths = {"profile", "status"})
  @Query("""
      select u
      from User u
      where u.id = :userId
      """)
  Optional<User> findWithProfileAndStatusById(@Param("userId") UUID userId);

  @EntityGraph(attributePaths = {"profile", "status"})
  @Query("""
      select u
      from User u
      """)
  List<User> findAllWithProfileAndStatus();

  Optional<User> findByEmail(String email);

  Optional<User> findByUsername(String username);

  boolean existsByEmail(String email);

  boolean existsByUsername(String username);
}