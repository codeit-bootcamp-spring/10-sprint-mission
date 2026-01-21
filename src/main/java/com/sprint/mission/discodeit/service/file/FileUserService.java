package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.*;

public class FileUserService implements UserService {
    FileUserRepository users = new FileUserRepository();

    @Override
    public User find(UUID id) {
        Set<User> usersInFile = users.fileLoad();
        return usersInFile.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElseThrow(() ->new RuntimeException("User not found: id = " + id));
    }

    @Override
    public Set<User> findAll() {
        return users.fileLoad();
    }

    @Override
    public User create(String userName) throws IOException {
        User user = new User(userName);
        Set<User> usersInFile = users.fileLoad();
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
        Set<User> usersInFile = users.fileLoad();

        User user = usersInFile.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElseThrow(() ->new RuntimeException("User not found: id = " + id));

        user.updateUserName(newUserName);
        users.fileSave(usersInFile);

        return user;
    }

    @Override
    public User update(UUID id, List<Role> roles) {
        Set<User> usersInFile = users.fileLoad();

        User user = usersInFile.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElseThrow(() ->new RuntimeException("User not found: id = " + id));

        user.updateRoles(roles);
        users.fileSave(usersInFile);

        return user;
    }


}
