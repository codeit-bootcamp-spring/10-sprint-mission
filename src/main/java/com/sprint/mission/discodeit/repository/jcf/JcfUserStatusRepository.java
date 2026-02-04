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

    private final Map<UUID, UserStatus> userStatuses = new LinkedHashMap<>();

    public void reset() {
        userStatuses.clear();
    }

    @Override
    public synchronized UserStatus save(UserStatus status) {
        userStatuses.put(status.getId(), status);
        return status;
    }

    @Override
    public synchronized Optional<UserStatus> findById(UUID id) {
        return Optional.ofNullable(userStatuses.get(id));
    }

    @Override
    public synchronized List<UserStatus> findAll() {
        return new ArrayList<>(userStatuses.values());
    }

    @Override
    public synchronized UserStatus findByUserId(UUID userId) {
        for (UserStatus status : userStatuses.values()) {
            if (status.getUserId().equals(userId)) return status;
        }
        return null;
    }

    @Override
    public synchronized void delete(UUID id) {
        userStatuses.remove(id);
    }

    @Override
    public synchronized void deleteByUserId(UUID userId) {
        userStatuses.entrySet().removeIf(e -> e.getValue().getUserId().equals(userId));
    }
}
