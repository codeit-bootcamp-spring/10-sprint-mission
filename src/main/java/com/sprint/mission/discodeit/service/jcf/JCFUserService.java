package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFUserService implements UserService {
    private final List<User> data;

    public JCFUserService() {
        this.data = new ArrayList<>();
    }


    @Override
    public User createUser(String username, String email) {
        User user = new User(username, email);
        data.add(user);
        return user;
    }

    @Override
    public User findById(UUID id) {
        for (User user : data) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data);
    }

    @Override
    public User update(UUID id, String username, String email) {
        for (User user : data) {
            if (user.getId().equals(id)) {
                user.update(id,username, email);
                return user;
            }
        }
        throw new IllegalArgumentException("User not found: " + id);
    }

    @Override
    public void delete(UUID id) {
        data.removeIf(user -> user.getId().equals(id));
    }
}
