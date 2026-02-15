package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatusEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository {
    // 사용자 상태 저장
    void save(UserStatusEntity userStatus);

    // 사용자 상태 단건 조회
    Optional<UserStatusEntity> findById(UUID userStatusId);

    // 특정 사용자 상태 조회
    UserStatusEntity findByUserId(UUID userId);

    // 사용자 상태 전체 조회
    List<UserStatusEntity> findAll();

    // 사용자 상태 삭제
    void delete(UserStatusEntity userStatus);

    // 유효성 검사 (중복 확인)
    boolean existsById(UUID userId);
}
