package com.sprint.mission.discodeit.repository.jcf;


import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
@ConditionalOnProperty(
        prefix = "discodeit.repository",
        name = "type",
        havingValue = "jcf",
        matchIfMissing = true
)
public class JcfUserStatusRepository implements UserStatusRepository {

    private final Map<UUID, UserStatus> store = new LinkedHashMap<>();

    @Override
    public synchronized UserStatus save(UserStatus status) {
        store.put(status.getId(), status);
        return status;
    }

    @Override
    public synchronized UserStatus findById(UUID id) {
        return store.get(id);
    }

    @Override
    public synchronized UserStatus findByUserId(UUID userId) {
        return store.values().stream()
                .filter(s -> s.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public synchronized List<UserStatus> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public synchronized void delete(UUID id) {
        store.remove(id);
    }

    @Override
    public synchronized void deleteByUserId(UUID userId) {
        store.values().removeIf(s -> s.getUserId().equals(userId));
    }
}