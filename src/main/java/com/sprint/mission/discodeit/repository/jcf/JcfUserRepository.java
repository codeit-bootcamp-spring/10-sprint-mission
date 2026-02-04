package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import com.sprint.mission.discodeit.repository.UserRepository;
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
public class JcfUserRepository implements UserRepository {

    private final Map<UUID, User> users = new LinkedHashMap<>();

    @Override
    public synchronized User save(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public synchronized User findById(UUID id) {
        return users.get(id);
    }

    @Override
    public synchronized List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public synchronized void delete(UUID id) {
        User removed = users.remove(id);
        if (removed == null) throw new UserNotFoundException();
    }

    @Override
    public synchronized boolean existsByName(String name) {
        for (User user : users.values()) {
            if (user.getName().equals(name)) return true;
        }
        return false;
    }

    @Override
    public synchronized boolean existsByEmail(String email) {
        for (User user : users.values()) {
            if (user.getEmail().equals(email)) return true;
        }
        return false;
    }
}
