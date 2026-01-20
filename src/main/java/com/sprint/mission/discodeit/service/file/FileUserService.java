package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

public class FileUserService implements UserService {

    @Override
    public User createUser(String name, String email) {
        return null;
    }

    @Override
    public User findUserById(UUID userId) {
        return null;
    }

    @Override
    public List<User> findAll() {
        return List.of();
    }

    @Override
    public User updateUserNickname(UUID userId, String nickname) {
        return null;
    }

    @Override
    public void deleteUser(UUID userId) {

    }
}
