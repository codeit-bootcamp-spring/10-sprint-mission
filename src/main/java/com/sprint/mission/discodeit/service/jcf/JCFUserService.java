package com.sprint.mission.discodeit.service.jcf;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class JCFUserService implements UserService {
    private static JCFUserService instance = null;
    private JCFUserService(){}
    public static JCFUserService getInstance() {
        if(instance == null){
            instance = new JCFUserService();
        }
        return instance;
    }

    private Set<User> users = new HashSet<>();

    @Override
    public User find(UUID id) {
        return users.stream()
                .filter(user -> id.equals(user.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User not found: id = " + id));
    }

    @Override
    public Set<User> findAll() {
        Set<User> newUsers = new HashSet<>();
        newUsers.addAll(users);
        return newUsers;
    }

    @Override
    public User create(String userName) {
        User user = new User(userName);
        users.add(user);
        return user;
    }

    @Override
    public void delete(UUID id) {
        User user = find(id);
        users.remove(user);
    }

    @Override
    public User update(UUID id, String newUserName) {
        User user = this.find(id);
        user.updateUserName(newUserName);
        return user;
    }

    @Override
    public User update(User user) {
        return null;
    }
}
