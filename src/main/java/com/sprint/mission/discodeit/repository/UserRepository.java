package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserRole;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, UUID> {

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);

  Optional<User> findByEmail(String email);

  @Query("""
      select distinct u
      from User u
      left join fetch u.profileImage
      left join fetch u.status
      where u.id in :userIds
      """)
  List<User> findAllByIdInWithProfileImageAndStatus(@Param("userIds") List<UUID> userIds);

  boolean existsByRole(UserRole role);
}