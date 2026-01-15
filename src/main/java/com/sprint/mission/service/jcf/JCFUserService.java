package com.sprint.mission.service.jcf;

import com.sprint.mission.entity.User;
import com.sprint.mission.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JCFUserService implements UserService {
    private final Map<UUID, User> data;

    public JCFUserService() {
        this.data = new HashMap<>();
    }

    @Override
    public User create(String value) {
        return create(value, data, User::new);
    }

    @Override
    public User get(UUID uuid) {
        return get(uuid, data);
    }

    @Override
    public List<User> getAll(List<UUID> uuids) {
        return getAll(uuids, data);
    }

    @Override
    public void update(UUID uuid, String newUserId) {
        update(uuid, newUserId, data);
    }

    @Override
    public void delete(UUID uuid) {
        delete(uuid, data);
    }
}
