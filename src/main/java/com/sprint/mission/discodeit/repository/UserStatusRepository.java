package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository {
    UserStatus save(UserStatus userStatus);

    Optional<UserStatus> findByUserId(UUID userId); // 특정 유저의 접속 상태 조회

    List<UserStatus> findAll();

    void deleteByUserId(UUID userId); // 유저 삭제 시 해당 유저의 접속 상태 데이터 삭제
}
