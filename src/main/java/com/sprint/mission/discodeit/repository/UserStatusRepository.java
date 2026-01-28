package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;
import java.util.Optional;

public interface UserStatusRepository {
    void save(UserStatus userStatus);
    Optional<UserStatus> findById(String id); //Optional은 데이터가 Null값인 경우 예외처리 위함
    Optional<UserStatus> findByUserId(String userId); //id 데이터의 주소값과 실제 유저 찾기
    void deleteById(String id);
}
