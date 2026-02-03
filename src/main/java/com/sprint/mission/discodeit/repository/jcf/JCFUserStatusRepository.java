package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
public class JCFUserStatusRepository implements UserStatusRepository {
    private final Map<UUID, UserStatus> storage = new HashMap<>();

    @Override
    public void save(UserStatus userStatus) { storage.put(userStatus.getId(), userStatus); }

    @Override
    public Optional<UserStatus> findById(UUID id) { return Optional.ofNullable(storage.get(id)); }

    @Override
    public void deleteById(UUID id) { storage.remove(id); }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return storage.values().stream().filter(s -> s.getUserId().equals(userId)).findFirst();
    }

    @Override
    public void deleteByUserId(UUID userId) {
        storage.values().removeIf(s -> s.getUserId().equals(userId));
    }
}