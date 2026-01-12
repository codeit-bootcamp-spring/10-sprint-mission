package com.sprint.mission.discodeit.service.jcf;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

public class JCFUser implements UserService {
    private static JCFUser instance = null;
    private JCFUser(){}
    public static JCFUser getInstance() {
        if(instance == null){
            instance = new JCFUser();
        }
        return instance;
    }

    private HashSet<User> users = new HashSet<>();

    @Override
    public User read(UUID id) {
        return users.stream()
                .filter(user -> user.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public HashSet<User> readAll() {
        return users;
    }

    @Override
    public User create(User user) {
        users.add(user);
        return user;
    }

    @Override
    public void delete(User user) {
        users.remove(user);
    }

    @Override
    public void update(UUID id, String str) {
        users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .ifPresent(user -> user.updateUserName(str));
    }
}
