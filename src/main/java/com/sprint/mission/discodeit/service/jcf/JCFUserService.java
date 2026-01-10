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
    public User create(String nickname, String email) {
        User user = new User(nickname, email);
        data.add(user);
        return user;
    }

    @Override
    public User read(UUID userId) {
        return data.stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    @Override
    public List<User> readAll() {
        return new ArrayList<>(data);
    }

    @Override
    public User update(UUID userId, String newNickname) {
        User user = read(userId);
        user.update(newNickname);
        return user;
    }

    @Override
    public void delete(UUID userId) {
        data.removeIf(u -> u.getId().equals(userId));
    }
}
