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
        if (userStatus == null) {
            throw new IllegalArgumentException("userStatus는 null일 수 없습니다.");
        }
        if (userStatus.getId() == null) {
            throw new IllegalArgumentException("userStatus.id는 null일 수 없습니다.");
        }
        data.put(userStatus.getId(), userStatus);
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("id는 null일 수 없습니다.");
        }
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<UserStatus> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
    }

    // 우리가 아까 추가한 메서드
    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        if (userId == null) throw new IllegalArgumentException("userId는 null일 수 없습니다.");

        for (UserStatus us : data.values()) {
            if (userId.equals(us.getUserId())) {
                return Optional.of(us);
            }
        }
        return Optional.empty();
    }
}
