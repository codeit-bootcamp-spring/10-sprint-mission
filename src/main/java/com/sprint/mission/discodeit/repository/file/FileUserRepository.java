package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public class FileUserRepository extends FileDomainRepository<User> implements UserRepository {

    public FileUserRepository() throws IOException {
        super(Paths.get(System.getProperty("user.dir"), "file-data-map", "User"),
                ".user");
    }

    @Override
    public User save(User user) throws IOException {
        return save(user, User::getId);
    }

    @Override
    public List<User> findAll() throws IOException {
        return streamAll(Stream::toList);
    }

    @Override
    public boolean existsByUsername(String username) throws IOException {
        return anyMatch(user -> user.matchUsername(username));
    }

    @Override
    public boolean existsByEmail(String email) throws IOException {
        return anyMatch(user -> user.matchEmail(email));
    }
}
