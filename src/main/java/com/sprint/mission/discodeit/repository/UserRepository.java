package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsernameOrEmailAndIdNot(String username, String email, UUID id);

    Optional<User> findByUsername(String username);

    @Query("SELECT u FROM User u JOIN FETCH u.status LEFT JOIN  FETCH  u.profile")
    List<User> findAllWithStatusAndProfile();
}
