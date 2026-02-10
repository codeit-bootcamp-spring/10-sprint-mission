package com.sprint.mission.discodeit.user.repository;

import com.sprint.mission.discodeit.user.entity.UserStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserStatusRepository {
    Optional<UserStatus> findByUserId(UUID userId);
    Optional<UserStatus> findById(UUID id);
    List<UserStatus> findAll();
    void save(UserStatus userStatus);
    void deleteById(UUID id);
}
