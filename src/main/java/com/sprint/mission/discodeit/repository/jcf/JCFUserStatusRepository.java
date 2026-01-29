package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf")
public class JCFUserStatusRepository implements UserStatusRepository {
    private final Map<UUID, UserStatus> data;

    public JCFUserStatusRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public UserStatus save(UserStatus UserStatus) {
        return this.data.put(UserStatus.getId(), UserStatus);
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        return Optional.ofNullable(this.data.get(id));
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID id) {
        return this.data.values().stream().
                filter(userStatus -> userStatus.getUserId().equals(id))
                .findFirst();
    }

    @Override
    public List<UserStatus> findAll() {
        return this.data.values().stream().toList();
    }

    @Override
    public boolean existsById(UUID id) {
        return this.data.values().stream()
                .anyMatch(userStatus -> userStatus.getUserId().equals(id));
    }

    @Override
    public void deleteById(UUID id) {
        this.data.remove(id);

    }

    @Override
    public void deleteByUserId(UUID id) {
        this.data.values()
                .removeIf(userStatus -> userStatus.getUserId().equals(id));
    }
}
