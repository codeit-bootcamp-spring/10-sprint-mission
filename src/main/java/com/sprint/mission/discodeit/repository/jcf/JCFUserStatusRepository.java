package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Profile("jcf")
public class JCFUserStatusRepository implements UserStatusRepository {
    private final Map<UUID, UserStatus> data = new HashMap<>();

    @Override
    public UserStatus save(UserStatus status){
        data.put(status.getId(), status);
        return status;
    }

    @Override
    public Optional<UserStatus> findById(UUID id) { return Optional.ofNullable(data.get(id)); }

    // 특정 유저의 접속 상태 조회
    @Override
    public Optional<UserStatus> findByUserId(UUID userId){
        return data.values().stream()
                .filter(us -> us.getUserId().equals(userId))
                .findFirst();
    }

    @Override
    public List<UserStatus> findAll(){
        return data.values().stream().toList();
    }

    @Override
    public void deleteById(UUID id) { data.remove(id); }

    // 유저 삭제 시 해당 유저의 접속 상태 데이터 삭제
    @Override
    public void deleteByUserId(UUID userId){
        data.values().removeIf(us -> us.getUserId().equals(userId));
    }
}
