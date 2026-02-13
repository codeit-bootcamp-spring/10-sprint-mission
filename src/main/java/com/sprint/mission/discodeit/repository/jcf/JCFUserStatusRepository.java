package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class JCFUserStatusRepository implements UserStatusRepository {

    private final Map<UUID, UserStatus> data;

    public JCFUserStatusRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public UserStatus save(UserStatus userStatus) {
        this.data.put(userStatus.getId(), userStatus);
        return userStatus;
    }

    @Override
    public Optional<UserStatus> findById(UUID Id) {
        return Optional.ofNullable(this.data.get(Id));
    }

    @Override
    public List<UserStatus> findAll() {
        return this.data.values().stream().toList();
    }

    @Override
    public boolean existsByUserId(UUID userId) {
        return this.data.values().stream().anyMatch(u -> u.getUserId().equals(userId));
    }

    @Override
    public void deleteById(UUID Id) {
        this.data.remove(Id);

    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return this.data.values().stream().filter(u -> u.getUserId().equals(userId)).findFirst();
    }

    @Override
    public void deleteByUserId(UUID userId) {
        this.data.values().removeIf(u -> u.getUserId().equals(userId));

    }
}
