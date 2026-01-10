package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private final Map<UUID, User> data;
    public JCFUserService() {
        this.data = new HashMap<>();
    }

    @Override
    public User createUser(String name, String email) {
        User user = new User(name, email);
        data.put(user.getId(), user);
        return user;
    }
    @Override
    public User getUser(UUID id) {
        return data.get(id);
    }
    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(data.values());
    }
    @Override
    public void updateUser(UUID id, String name, String email) {
        User user = data.get(id);
        user.update(name, email);
    }
    @Override
    public void deleteUser(UUID id) {
        data.remove(id);
    }
}
