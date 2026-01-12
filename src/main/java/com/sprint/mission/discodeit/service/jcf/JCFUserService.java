package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFUserService implements UserService {
    private final List<User> data = new ArrayList<>();

    @Override
    public User createUser(String userName, String password, String email) {
        User user = new User(userName, password, email);
        data.add(user);
        return user;
    }

    @Override
    public User getUser(UUID userId) {
        return findById(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return List.copyOf(data);
    }

    @Override
    public User updateUser(UUID userId, String userName, String password, String email) {
        User oldUser = findById(userId);
        oldUser.updateUserName(userName);
        oldUser.updatePassword(password);
        oldUser.updateEmail(email);
        return oldUser;
    }

    @Override
    public User deleteUser(UUID userId) {
        User target = findById(userId);
        target.getChannels().forEach(channel -> channel.removeUser(target));
        data.remove(target);
        return target;
    }

    private User findById(UUID id) {
        return data.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("해당 사용자가 존재하지 않습니다."));
    }
}
