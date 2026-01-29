package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class JCFUserStatusRepository implements UserStatusRepository {
    private final Map<UUID, UserStatus> data = new ConcurrentHashMap<>();

    @Override
    public UserStatus save(UserStatus status){
        data.put(status.getUserId(), status);
        return status;
    }

    // 특정 유저의 접속 상태 조회
    @Override
    public Optional<UserStatus> findByUserId(UUID userId){
        return Optional.ofNullable(data.get(userId));
    }

    // 유저 삭제 시 해당 유저의 접속 상태 데이터 삭제
    @Override
    public void deleteByUserId(UUID userId){
        data.remove(userId);
    }
}
