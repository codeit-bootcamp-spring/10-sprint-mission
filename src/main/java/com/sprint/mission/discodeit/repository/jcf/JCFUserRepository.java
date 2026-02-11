package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
public class JCFUserRepository implements UserRepository {
    private final Map<UUID, User> data = new HashMap<>();

    @Override
    public User save(User user) {
        this.data.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(UUID userId) {
        return Optional.ofNullable(this.data.get(userId));
    }

    @Override
    public List<User> findAll() {
        return this.data.values().stream().toList();
    }

    @Override
    public void deleteById(UUID userId) {
        data.remove(userId);
    }
}
