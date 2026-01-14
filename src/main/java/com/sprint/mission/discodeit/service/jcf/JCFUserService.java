package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.time.LocalDate;
import java.util.*;

public class JCFUserService implements UserService {
    private final Map<UUID, User> userStore;

    public JCFUserService(){
        this.userStore = new HashMap<>();
    }

    public User registerUser(String name, String email, java.time.LocalDate birthDate, String phoneNumber, String password){
            User newUser = new User(name, email, birthDate, phoneNumber, password);
            userStore.put(newUser.getId(), newUser);
            return newUser;
        }
     public User findUserById(UUID userId) {
            return userStore.get(userId);
        }

    @Override
    public List<User> findAllUser() {
        return new ArrayList<>(userStore.values());
    }

    public void deleteUser(UUID userId){
            userStore.remove(userId);
        }

    public int userCount () {return userStore.size();}



}
