package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private final List<User> data = new ArrayList<>();

    @Override
    public User createUser(String userName, String password, String email) {
        validateUserExist(userName);
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
        validateUserExist(userName);
        User findUser = findById(userId);
        Optional.ofNullable(userName)
                .ifPresent(findUser::updateUserName);
        Optional.ofNullable(password)
                .ifPresent(findUser::updatePassword);
        Optional.ofNullable(email)
                .ifPresent(findUser::updateEmail);
        return findUser;
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
                .orElseThrow(() -> new NoSuchElementException("해당 사용자가 존재하지 않습니다."));
    }

    private void validateUserExist(String userName) {
        Optional<User> target = data.stream()
                .filter(u -> u.getUserName().equals(userName))
                .findFirst();
        if(target.isPresent()) throw new IllegalStateException("이미 존재하는 사용자 이름입니다.");
    }
}
