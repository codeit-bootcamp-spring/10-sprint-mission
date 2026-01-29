package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
@Repository
public class JCFUserStatusRepository implements UserStatusRepository {

    private final Map<UUID, UserStatus> data = new HashMap<>();

    @Override
    public void save(UserStatus userStatus) {
        data.put(userStatus.getId(), userStatus);
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<UserStatus> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void deleteById(UUID id) {
        data.remove(id);
    }

    // 우리가 아까 추가한 메서드
    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return data.values().stream()
                .filter(status -> status.getUserId().equals(userId))
                .findFirst();
    }
}
