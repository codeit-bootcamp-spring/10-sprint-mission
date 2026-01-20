package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFUserRepository implements UserRepository {
    private List<User> userData;

    public JCFUserRepository() {
        userData = new ArrayList<>();
    }

    @Override
    public User find(UUID userID) {
        return userData.stream()
                .filter(user -> user.getId().equals(userID))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userID));
    }

    @Override
    public List<User> findAll() {
        return userData;
    }

    @Override
    public void addUser(User user) {
        userData.add(user);
    }

    @Override
    public void removeUser(User user) {
        userData.remove(user);
    }

    @Override
    public User save(User user){
        userData.removeIf(ch -> ch.getId().equals(user.getId()));
        userData.add(user);
        return user;
    }
}
