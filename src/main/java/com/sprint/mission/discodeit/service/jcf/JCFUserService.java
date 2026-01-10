package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {

    List<User> data = new ArrayList<>();

    @Override
    public User createUser(String name, String email, String userId) {
        if (name == null || email == null || userId == null){
            return null;
        }

        boolean isRedundant = data.stream().anyMatch(user -> user.getUserId().equals(userId));
        if(isRedundant){
            return null;
        }

        User user = new User(name, email, userId);
        data.add(user);
        return user;
    }

    @Override
    public User readUser(String userId) {
        return data.stream().filter(user -> userId.equals(user.getUserId()))
                .findFirst()
                .orElse(null);
    }

    public User readUser(UUID id){
        return data.stream().filter(user -> id.equals(user.getId()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean updateUser(String userId, String name, String email) {
        Optional<User> target = data.stream().filter(user -> userId.equals(user.getUserId()))
                .findFirst();

        if(target.isEmpty()){
            return false;
        }

        User user = target.get();
        if(name != null){
            user.updateName(name);
        }
        if(email != null){
            user.updateEmail(email);
        }

        return true;
    }

    

    @Override
    public boolean deleteUser(String userId) {
        Optional<User> target = data.stream().filter(user -> userId.equals(user.getUserId()))
                .findFirst();

        if(target.isEmpty()){
            return false;
        }
        User user = target.get();
        data.remove(user);
        return true;
    }

    @Override
    public ArrayList<User> readAllUsers() {
        data.forEach(System.out::println);
        return (ArrayList<User>) data;

    }


}