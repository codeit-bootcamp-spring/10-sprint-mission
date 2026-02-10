package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
public class JCFUserRepository implements UserRepository {

    private final List<User> data = new ArrayList<>();

    @Override
    public User save(User user) {
        delete(user);
        data.add(user);
        return user;
    }

    @Override
    public Optional<User> findById(UUID userId) {
        return data.stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return List.copyOf(data);
    }

    @Override
    public void delete(User user) {
        data.removeIf(u -> u.equals(user));
    }
}
