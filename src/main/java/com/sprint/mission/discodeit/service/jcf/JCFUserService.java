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
        if (name == null || name.isBlank()) throw new IllegalArgumentException("이름은 필수입니다.");
        if (email == null || email.isBlank()) throw new IllegalArgumentException("이메일은 필수입니다.");
        User user = new User(name, email);
        data.put(user.getId(), user);
        return user;
    }
    @Override
    public User getUser(UUID id) {
        validateUserId(id);
        return data.get(id);
    }
    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(data.values());
    }
    @Override
    public void updateUser(UUID id, String name, String email) {
        validateUserId(id);
        User user = data.get(id);
        if(user != null){
            if(name != null){
                user.updateName(name);
            }
            if(email != null){
                user.updateEmail(email);
            }
        }
    }
    @Override
    public void deleteUser(UUID id) {
        validateUserId(id);
        data.remove(id);
    }

    @Override
    public void validateUser(User user) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("유저 정보가 없습니다.");
        }
        if (data.get(user.getId()) == null) {
            throw new IllegalArgumentException("존재하지 않는 유저입니다.");
        }
    }
    private void validateUserId(UUID id) {
        if (id == null) throw new IllegalArgumentException("유저 ID가 없습니다.");
        if (!data.containsKey(id)) throw new IllegalArgumentException("존재하지 않는 유저입니다.");
    }
}
