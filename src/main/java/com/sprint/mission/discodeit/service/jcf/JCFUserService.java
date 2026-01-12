package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {

    public List<User> data = new ArrayList<>();

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
        User user = data.stream()
                .filter(u -> userId.equals(u.getUserId()))
                .findFirst()
                .orElse(null);
        System.out.println(user);
        return user;
    }

    public User readUser(UUID id){
        User user = data.stream()
                .filter(u -> id.equals(u.getId()))
                .findFirst()
                .orElse(null);

        System.out.println(user);

        return user;
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
    public boolean deleteUser(UUID uuId) {
        Optional<User> target = data.stream().filter(user -> uuId.equals(user.getId()))
                .findFirst();

        if(target.isEmpty()){
            return false;
        }
        User user = target.get();
        data.remove(user);
        return true;
    }

    public boolean deleteUser(String userId){
        Optional<User> target = data.stream()
                .filter(user -> userId.equals(user.getUserId()))
                .findFirst();

        if(target.isEmpty()){
            return false;
        }
        data.remove(target.get());
        return true;
    }

    @Override
    public ArrayList<User> readAllUsers() {
        System.out.println("-".repeat(20) + " 전체 조회 " + "-".repeat(20) );
        data.forEach(System.out::println);
        System.out.println("-".repeat(50) );
        return (ArrayList<User>) data;

    }


}