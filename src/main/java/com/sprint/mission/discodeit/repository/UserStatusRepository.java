package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository extends JpaRepository<UserStatus, UUID> {
//    UserStatus save(UserStatus userStatus);
//    Optional<UserStatus> findById(UUID id);
//    List<UserStatus> findAll();
//    boolean existsById(UUID id);
//    void delete(UUID id);
    Optional<UserStatus> findByUserId(UUID userId);
    boolean existsByUserId(UUID userId);
    void deleteByUserId(UUID userId);
}
