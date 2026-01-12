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
        if (user == null) {
            throw new IllegalArgumentException("생성하고자 하는 유저가 null일 수 없음");
        }
        users.put(user.getId(), user);
    }

    @Override
    public User findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("찾고자 하는 유저의 id는 null 일 수 없음");
        }
        User user = users.get(id);
        if (user == null) {
            throw new IllegalStateException("해당 id의 사용자를 찾을 수 없음");
        }
        return user;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void updateById(UUID id, String newUserName) {
        User targetUser = findById(id);
        if (newUserName == null) {
            throw new IllegalArgumentException("변경 하려는 유저의 이름이 null일 수 없음");
        }
        targetUser.setUserName(newUserName);
    }

    @Override
    public void deleteById(UUID id) {
        findById(id);
        users.remove(id);
    }

    @Override
    public void printAllUsers() {
        for (User user : users.values()) {
            System.out.println(user);
        }
    }
}
