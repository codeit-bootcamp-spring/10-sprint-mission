package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Repository;

import java.util.*;
@Repository
@ConditionalOnProperty(name ="discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
public class JCFUserStatusRepository implements UserStatusRepository {
    private final Map<UUID, UserStatus> data;

    public JCFUserStatusRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public UserStatus save(UserStatus userStatus) {
        return data.put(userStatus.getId(), userStatus);
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return data.values()
                .stream()
                .filter(s->s.getUserId().equals(userId))
                .findFirst();
    }

    @Override
    public Optional<UserStatus> find(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<UserStatus> findAll() {
        return data.values().stream().toList();
    }

    @Override
    public boolean existsByUserId(UUID userId) {
        return data.values()
                .stream()
                .anyMatch(s->s.getUserId().equals(userId));
    }

    @Override
    public boolean existsById(UUID id) {
        return data.containsKey(id);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        data.values().removeIf(s->s.getUserId().equals(userId));
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
    }
}

