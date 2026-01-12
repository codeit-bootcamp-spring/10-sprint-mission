package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private final Map<UUID, User> userMap = new HashMap<>();

    // 검증 및 조회 로직 분리
    private User findUserByIdOrThrow(UUID id) {
        if (!userMap.containsKey(id)) {
            throw new IllegalArgumentException("해당 ID의 사용자가 존재하지 않습니다. ID: " + id);
        }
        return userMap.get(id);
    }

    // Service Implementation
    // Create
    @Override
    public User createUser(String username) {
        // 중복 이름 검사
        boolean isDuplicate = userMap.values().stream()
                .anyMatch(user -> user.getUsername().equals(username));
        if (isDuplicate) {
            throw new IllegalArgumentException("이미 존재하는 사용자 이름입니다: " + username);
        }

        User user = new User(username);
        userMap.put(user.getId(), user);

        System.out.println("User Created: " + user.toString());
        return user;
    }

    // Read
    @Override
    public User findUserByUserId(UUID id) {
        return findUserByIdOrThrow(id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<User>(userMap.values());
    }

    // Update
    @Override
    public User updateUser(UUID id, String newUsername) {
        User user = findUserByIdOrThrow(id);

        // 중복 이름 체크 (자기 자신은 제외하고 체크)
        boolean isDuplicate = userMap.values().stream()
                .anyMatch(u -> (!u.getId().equals(id)) && (u.getUsername().equals(newUsername)) );

        if (isDuplicate) {
            throw new IllegalArgumentException("이미 존재하는 이름입니다.");
        }

        user.updateUsername(newUsername);
        return user;
    }

    // Delete
    @Override
    public void deleteUser(UUID id) {
        findUserByIdOrThrow(id);
        userMap.remove(id);
    }
}