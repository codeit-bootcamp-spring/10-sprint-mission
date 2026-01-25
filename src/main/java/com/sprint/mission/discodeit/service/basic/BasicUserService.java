package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.*;

public class BasicUserService implements UserService {
    UserRepository users;

    public BasicUserService(UserRepository users){
        this.users = users;
    }

    @Override
    public User find(UUID id) {
        return users.fileLoad(id);
    }

    @Override
    public Set<User> findAll() {
        return users.fileLoadAll();
    }

    @Override
    public User create(String userName) throws IOException {
        User user = new User(userName);
        Set<User> usersInFile = users.fileLoadAll();
        usersInFile.add(user);
        users.fileSave(usersInFile);

        return user;
    }

    @Override
    public void delete(UUID id) {
        users.fileDelete(id);
    }

    @Override
    public User update(UUID id, String newUserName) {
        Set<User> usersInFile = users.fileLoadAll();

        User user = usersInFile.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElseThrow(() ->new RuntimeException("User not found: id = " + id));

        user.updateUserName(newUserName);
        users.fileSave(usersInFile);

        return user;
    }

    @Override
    public User update(UUID id, List<UUID> roles) {
        Set<User> usersInFile = users.fileLoadAll();

        User user = usersInFile.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElseThrow(() ->new RuntimeException("User not found: id = " + id));

        user.setRoleIDs(roles);
        users.fileSave(usersInFile);

        return user;
    }


}
