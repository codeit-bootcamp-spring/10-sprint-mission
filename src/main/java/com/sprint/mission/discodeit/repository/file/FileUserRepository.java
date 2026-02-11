package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileUserRepository extends AbstractFileRepository<User> implements UserRepository {

    public FileUserRepository(@Value("${discodeit.repository.file-directory:.discodeit}")String directoryPath) {
        super(directoryPath + File.separator + "User.ser");
    }

    @Override
    public Optional<User> findByName(String name) {
        Map<UUID, User> data = load();
        Optional<User> user = data.values().stream()
                .filter(u -> u.getName().equals(name))
                .findFirst();
        return user;
    }

    @Override
    public Optional<User> findByEmail(String email){
        Map<UUID, User> data = load();
        Optional<User> user = data.values().stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst();
        return user;
    }
}
