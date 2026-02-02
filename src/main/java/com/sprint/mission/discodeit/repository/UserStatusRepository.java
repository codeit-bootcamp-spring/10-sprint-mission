package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserStatusRepository {
    void save(UserStatus userStatus);
    Optional<UserStatus> findById(UUID id); //Optional은 데이터가 Null값인 경우 예외처리 위함
    Optional<UserStatus> findByUserId(UUID userId); //id 데이터의 주소값과 실제 유저 찾기
    void deleteById(UUID id);
    void deleteByUserId(UUID userId);
}
