package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, UUID> {

  Optional<User> findByEmail(String email);

  Optional<User> findByUsername(String username);

  Optional<User> findByRole(Role role);

  boolean existsByRole(Role role);

  // 단일 상세 조회 (BasicUserService.findById 등에서 사용)
  @EntityGraph(attributePaths = "profile")
  @Query("SELECT u FROM User u WHERE u.id = :id")
  Optional<User> findWithDetailsById(@Param("id") UUID id);

  // 리스트 상세 조회 (ChannelService.createPrivateChannel에서 사용)
  @EntityGraph(attributePaths = "profile")
  List<User> findAllWithDetailsByIdIn(Collection<UUID> ids);

  @EntityGraph(attributePaths = "profile")
  @Query("SELECT u FROM User u")
  List<User> findAllWithDetails();
}
