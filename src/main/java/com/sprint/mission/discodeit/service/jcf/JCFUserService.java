package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFUserService implements UserService {
    ArrayList<User> list = new ArrayList<>();

    @Override
    public User createUser(String userName, String userEmail, String userPassword) {
        User user = new User(userName, userEmail, userPassword);
        list.add(user);
        return user;
    }

    @Override
    public User readUser(UUID id) {
        for (User user : list) {
            if(id.equals(user.getId())){
                return user;
            }
        }
        return null;
    }

    @Override
    public List<User> readAllUser() {
       return list;
    }

    @Override
    public void updateUser(UUID id, String userName, String userEmail, String userPassword) {
        readUser(id).updateUserName(userName);
        readUser(id).updateUserEmail(userEmail);
        readUser(id).updateUserPassword(userPassword);
    }

    public boolean isUserDeleted(UUID id) {
        for (User user : list) {
            if(id.equals(user.getId())) {
                return false;
            }
        }
        return true;
    }


    @Override
    public void deleteUser(UUID id) {
        list.remove(readUser(id));
    }
}
