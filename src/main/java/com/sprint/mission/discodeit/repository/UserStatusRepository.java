package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserStatusRepository {

    UserStatus save(UserStatus userStatus);

    Optional<UserStatus> findById(UUID userStatusId);

    Optional<UserStatus> findByUserId(UUID userId);

    List<UserStatus> findAll();

    boolean existsByUserId(UUID userId);

    void deleteById(UUID userStatusId);
}
