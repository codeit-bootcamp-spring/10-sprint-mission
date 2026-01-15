package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private final Map<UUID, User> data;
    public JCFUserService() {
        this.data = new HashMap<>();
    }

    @Override
    public User createUser(String name, String email) {
        validateUserInput(name, email);
        User user = new User(name, email);
        data.put(user.getId(), user);
        return user;
    }
    @Override
    public User getUser(UUID id) {
        return validateUserId(id);
    }
    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(data.values());
    }
    @Override
    public User updateUser(UUID id, String name, String email) {
        User user = validateUserId(id);
        Optional.ofNullable(name)
                .filter(n -> !n.isBlank())
                .ifPresent(user::updateName);

        Optional.ofNullable(email)
                .filter(e -> !e.isBlank())
                .ifPresent(user::updateEmail);
        return user;
    }
    @Override
    public void deleteUser(UUID id) {
        validateUserId(id);
        data.remove(id);
    }

    public void validateUserInput(String name, String email){
        if (name == null || name.isBlank()) throw new IllegalArgumentException("이름은 필수입니다.");
        if (email == null || email.isBlank()) throw new IllegalArgumentException("이메일은 필수입니다.");
    }
    private User validateUserId(UUID id) {
        return Optional.ofNullable(data.get(id))
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저 입니다."));
    }
}
