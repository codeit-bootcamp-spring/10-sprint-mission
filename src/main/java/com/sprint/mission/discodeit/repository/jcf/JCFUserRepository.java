package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JCFUserRepository implements UserRepository {
    private final Map<UUID, User> data;

    public JCFUserRepository() {
        this.data = new HashMap<>();
    }

    public User saveUser(User user) {
        data.put(user.getId(), user);
        return user;
    }

    public User findUserById(UUID userId) {
        return data.get(userId);
    }

    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    public void deleteUser(UUID userId) {
        data.remove(userId);
    }
}
