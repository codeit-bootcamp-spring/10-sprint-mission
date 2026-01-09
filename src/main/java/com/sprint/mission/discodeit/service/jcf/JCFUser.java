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
    public void read(UUID id) {
        for(User user : users) {
            if (user.getId() == id){
                System.out.println(user);
                return;
            }
        }
    }

    @Override
    public void readAll() {
        users.forEach(System.out::println);
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
    public void update(User originuser, User afteruser) {
        if (originuser == null || originuser.getId() == null) {
            return;
        }

        for(User user : users) {
            if (user.getId() == originuser.getId()){
                user.updateUserName(afteruser.getUserName());
            }
        }
    }
}
