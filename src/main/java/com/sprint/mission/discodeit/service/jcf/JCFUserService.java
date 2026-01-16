package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private final Map<UUID, User> users = new HashMap<>();

    @Override
    public User createUser(String username) {
        User user = new User(username);
        users.put(user.getId(), user);

        return user;
    }

    @Override
    public List<User> getUserList() {
        return users.values().stream()
                .toList();
    }

    @Override
    public User getUserInfoByUserId(UUID userId) {
        return findUserById(userId);
    }

    // id 를 기준으로 수정
    @Override
    public User updateUserName(UUID userId, String newName) {
        Objects.requireNonNull(newName, "username은 null일 수 없습니다.");

        User user = findUserById(userId);

        user.updateUsername(newName);
        return user;
    }

    @Override
    public void deleteUser(UUID userId) {
        User user = findUserById(userId);

        users.remove(userId);
    }

    private User findUserById(UUID userId) {
        Objects.requireNonNull(userId, "userId는 null일 수 없습니다.");

        User user = users.get(userId);

        if (user == null) {
            throw new NoSuchElementException("해당 id를 가진 유저가 존재하지 않습니다.");
        }

        return user;
    }
}
