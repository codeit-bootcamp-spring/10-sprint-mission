package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

import java.util.*;

public class JCFUserStateRepository implements UserStatusRepository {
    private final Map<UUID, UserStatus> data;

    public JCFUserStateRepository() {
        this.data = new HashMap<>();
    }


    @Override
    public UserStatus save(UserStatus userStatus) {
        this.data.put(userStatus.getId(), userStatus);
        return userStatus;
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
        return this.data.containsKey(id);
    }

    @Override
    public boolean existsByUserId(UUID userId) {
        return this.data.values().stream()
                .anyMatch(userStatus -> userStatus.getUserId().equals(userId));
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
