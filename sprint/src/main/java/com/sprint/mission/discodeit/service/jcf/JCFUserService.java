package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.service.UserService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JCFUserService implements UserService {
    private final Map<UUID, User> data;

    public JCFUserService() {
        data = new HashMap<>();
    }

    @Override
    public UUID createUser(String username, String email) {
        User user = new User(username, email);
        UUID id = user.getId();
        data.put(id, user);

        return id;
    }

    @Override
    public User getUser(UUID id) {
        return data.get(id);
    }

    @Override
    public Iterable<User> getAllUsers() {
        return data.values();
    }

    @Override
    public void updateUser(UUID id, String username, String email) {
        User user = getUser(id);
        if (user == null) {
            throw new NotFoundException("User " + id + "을 찾을 수 없었습니다.");
        }

        user.updateEmail(email);
        user.updateUsername(username);
    }

    @Override
    public void deleteUser(UUID id) {
        if (!data.containsKey(id)) {
            throw new NotFoundException("User " + id + "는 이미 삭제되었거나 없는 user입니다.");
        }

        data.remove(id);
    }
}
