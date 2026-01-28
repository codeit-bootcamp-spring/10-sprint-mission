package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.util.FileIo;
import org.springframework.stereotype.Repository;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class FileUserRepository implements UserRepository {
    public static final Path USER_DIRECTORY = Paths.get(System.getProperty("user.dir"), "data", "users");

    private final FileIo<User> fileIo;

    private FileUserRepository() {
        fileIo = new FileIo<User>(USER_DIRECTORY);
        fileIo.init();
    }

    @Override
    public User save(User user) {
        return fileIo.save(user.getId(), user);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return fileIo.load().stream()
            .filter(u -> u.getId().equals(id))
            .findFirst();
    }

    @Override
    public Optional<User> findByUserName(String userName) {
        return fileIo.load().stream()
            .filter(u -> u.getUserName().equals(userName))
            .findFirst();
    }

    @Override
    public List<User> findAll() {
        return fileIo.load();
    }

    @Override
    public void delete(UUID userId) throws RuntimeException {
        fileIo.delete(userId);
    }
}
