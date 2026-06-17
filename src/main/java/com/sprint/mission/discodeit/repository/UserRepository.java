package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);

    @NonNull
    @EntityGraph(attributePaths = {"profile", "status"})
    List<User> findAll();

    boolean existsByRole(Role role);

    List<User> findAllByRole(Role role);
}
