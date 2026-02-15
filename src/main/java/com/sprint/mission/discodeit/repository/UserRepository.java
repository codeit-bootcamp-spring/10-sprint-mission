package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    // 사용자 저장
    void save(UserEntity user);

    // 사용자 단건 조회
    Optional<UserEntity> findById(UUID userId);

    // 사용자 전체 조회
    List<UserEntity> findAll();

    // 사용자 삭제
    void delete(UserEntity user);

    // 유효성 검사 (이메일 중복)
    boolean existsByEmail(String email);

    // 유효성 검사 (이름 중복)
    boolean existsByNickname(String nickname);
}
