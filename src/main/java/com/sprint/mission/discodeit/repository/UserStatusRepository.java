package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository {

    UserStatus save(UserStatus userStatus);
    Optional<UserStatus> findById(UUID Id);
    List<UserStatus> findAll();//관리자 입장에서 모든 사용자 상태 조회/test용
    boolean existsById(UUID Id);
    void deleteById(UUID Id);

    Optional<UserStatus> findByUserId(UUID userId);
    void deleteByUserId(UUID userId);
}
