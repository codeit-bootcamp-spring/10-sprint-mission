package com.sprint.mission.discodeit.service.jcf;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.HashSet;
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

    private HashSet<User> users = new HashSet<>();

    @Override
    public User find(UUID id) {
        return users.stream()
                .filter(user -> user.getId() == id)
                .findFirst()
                .orElseThrow(() -> {throw new RuntimeException("Channel not found: id = " + id);}
                );
    }

    @Override
    public HashSet<User> findAll() {
        HashSet<User> newUsers = new HashSet<>();
        for(User user : users){
            newUsers.add(user);
        }
        return newUsers;
    }

    @Override
    public User create(User user) {
        users.add(user);
        return user;
    }

    @Override
    public void delete(UUID id) {
        users.remove(find(id));
    }

    @Override
    public User update(UUID id, String newUserName) {
        this.find(id)
                .updateUserName(newUserName);
        return this.find(id);
    }
}
