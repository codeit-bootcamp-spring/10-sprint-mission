package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository {
    UserStatus save(UserStatus userStatus);
    Optional<UserStatus> findById(UUID id);
    List<UserStatus> findAll();
    Optional<UserStatus> findByUserId(UUID userId);  // 유저 id로 유저 상태 조회
    void deleteById(UUID id);
    void deleteByUserId(UUID userId);

    boolean existsByUserId(UUID userId);
}
