package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.auth.enums.Role;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, UUID> {

  boolean existsByEmail(String email);

  boolean existsByUsername(String username);

  @Query("select u from User u left join fetch u.profile")
  List<User> findAllFetchUserInfo();

  @Query("select u from User u left join fetch u.profile where u.id in :ids")
  List<User> findAllByIdFetchUserInfo(List<UUID> ids);

  @Query("select u from User u left join fetch u.profile where u.id = :id")
  Optional<User> findByIdFetchUserInfo(UUID id);

  @Query("select u from User u left join fetch u.profile where u.username = :username")
  Optional<User> findByUsername(String username);

  boolean existsByRole(Role role);
}
