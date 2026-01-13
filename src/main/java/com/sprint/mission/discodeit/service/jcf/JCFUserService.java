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
    public User create(String name, String email)
    {
        User user = new User(name, email);
        data.add(user);
        return user;
    }

    @Override
    public User read(UUID id) {
        return data.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<User> readAll() {
        return new ArrayList<>(data);
    }

    @Override
    public void update(UUID id, String name, String email) {
        User user = read(id);
        if(user != null) {
            user.update(name, email);
        }
    }

    @Override
    public void updateName(UUID id, String name) {
        User user = read(id);
        if(user != null) {
            user.updateName(name);
        }
    }

    @Override
    public void updateEmail(UUID id, String email) {
        User user = read(id);
        if(user != null) {
            user.updateEmail(email);
        }
    }

    @Override
    public void delete(UUID id) {
        data.removeIf(user -> user.getId().equals(id));
    }

}
