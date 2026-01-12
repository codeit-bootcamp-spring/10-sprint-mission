package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private final Map<UUID, User> users;

    public JCFUserService() {
        users = new HashMap<>();
    }
    @Override
    public void createUser(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public User findById(UUID id) {
        return users.get(id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void updateById(UUID id, String newUserName) {
        User targetUser = findById(id);
        targetUser.setUserName(newUserName);
    }

    @Override
    public void deleteById(UUID id) {
        users.remove(id);
    }

    @Override
    public void printAllUsers() {
        for (User user : users.values()) {
            System.out.println(user);
        }
    }
}
