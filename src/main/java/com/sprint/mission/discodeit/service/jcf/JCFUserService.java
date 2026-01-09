package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private final Map<UUID, User> userMap = new HashMap<>();

    @Override
    public User createUser(String username) {
        // Map의 values()를 스트림으로 돌려서 같은 이름이 있는지 확인
        boolean isDuplicate = userMap.values().stream()
                .anyMatch(user -> user.getUsername().equals(username));

        if (isDuplicate) {
            throw new IllegalArgumentException("이미 존재하는 사용자 이름 : " + username);
        }

        User user = new User(username);
        userMap.put(user.getId(), user);

        System.out.println("User Created: " + user.getId());
        return user;
    }

    @Override
    public Optional<User> findOne(UUID id) {
        return Optional.ofNullable(userMap.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public User updateUser(UUID id, String newUsername) {
        User user = findOne(id)
                .orElseThrow(() -> new IllegalArgumentException("수정할 사용자를 찾을 수 없음"));

        boolean isDuplicate = userMap.values().stream()
                .anyMatch(u -> !u.getId().equals(id) && u.getUsername().equals(newUsername));

        if (isDuplicate) {
            throw new IllegalArgumentException("이미 존재하는 이름");
        }

        user.updateUsername(newUsername);

        return user;
    }

    @Override
    public void deleteUser(UUID id) {
        if (!userMap.containsKey(id)) {
            throw new IllegalArgumentException("삭제할 사용자가 존재하지 않음");
        }

        userMap.remove(id);
    }
}