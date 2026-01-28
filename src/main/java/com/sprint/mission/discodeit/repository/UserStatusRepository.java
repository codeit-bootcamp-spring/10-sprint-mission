package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.UUID;

public interface UserStatusRepository {
    // 사용자 상태 저장
    void save(UserStatus userStatus);

    // 사용자 상태 단건 조회
    UserStatus findById(UUID userStatusId);

    // 사용자 상태 삭제
    void delete(UUID userStatusId);
}
