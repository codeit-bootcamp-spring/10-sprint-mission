package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private final Map<UUID, User> data;

    public JCFUserService() {
        this.data = new HashMap<>();
    }

    public User create(String username) {
        User user = new User(username);
        data.put(user.getId(), user);
        return user;
    }

    public User findById(UUID userId) {
        return data.get(userId);
    }

    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    public void update(UUID userId, String username) {
        User user = data.get(userId);
        user.updateUsername(username);
    }

    public void delete(UUID userId) {
        data.remove(userId);
    }
}
