package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.service.UserService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class JCFUserService implements UserService {
    private final Map<UUID, User> data;

    private static JCFUserService instance = null;

    public JCFUserService() {
        data = new HashMap<>();
    }

    public static JCFUserService getInstance() {
        if (instance == null) {
            instance = new JCFUserService();
        }
        return instance;
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
    public User getUserByUsername(String username) {
        Optional<Map.Entry<UUID, User>> result = data.entrySet().stream()
                .filter(u -> u.getValue().getUsername().equals(username))
                .findFirst();
        return result.map(Map.Entry::getValue).orElse(null);
    }

    @Override
    public User getUserByEmail(String email) {
        Optional<Map.Entry<UUID, User>> result = data.entrySet().stream()
                .filter(u -> u.getValue().getEmail().equals(email))
                .findFirst();
        return result.map(Map.Entry::getValue).orElse(null);
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
