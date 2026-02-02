package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository {
    // 사용자 상태 저장
    void save(UserStatus userStatus);

    // 사용자 상태 단건 조회
    Optional<UserStatus> findById(UUID userStatusId);

    // 사용자 상태 전체 조회
    List<UserStatus> findAll();

    // 사용자 상태 삭제
    void delete(UserStatus userStatus);

    // 유효성 검사 (중복 확인)
    boolean existsById(UUID userId);
}
