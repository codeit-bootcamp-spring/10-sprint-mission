package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

public interface UserRepository extends JpaRepository<User, UUID> {

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);

  Optional<User> findByUsername(String username);

  @NonNull
  @Query("SELECT u FROM User u LEFT JOIN FETCH u.profile")
  List<User> findAll();

  @NonNull
  @Query("SELECT u FROM User u")
  List<User> findAllById(@NonNull Iterable<UUID> ids);
}
