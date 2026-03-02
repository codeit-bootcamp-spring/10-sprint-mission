package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.utils.FileIOHelper;
import com.sprint.mission.discodeit.utils.FileLockProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileUserRepository extends FileRepositoryLockSupport implements UserRepository {

    private static final Path USER_DIRECTORY =
            FileIOHelper.resolveDirectory("users");

    public FileUserRepository(FileLockProvider fileLockProvider) {
        super(fileLockProvider);
    }

    @Override
    public void save(User user) {
        Path userFilePath = USER_DIRECTORY.resolve(user.getId().toString());

        withLock(USER_DIRECTORY, () -> FileIOHelper.save(userFilePath, user));
    }

    @Override
    public Optional<User> findById(UUID id) {
        Path userFilePath = USER_DIRECTORY.resolve(id.toString());

        return withLock(USER_DIRECTORY, () -> FileIOHelper.load(userFilePath));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return withLock(USER_DIRECTORY, () -> FileIOHelper.<User>loadAll(USER_DIRECTORY).stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst());
    }

    @Override
    public List<User> findAll() {
        return withLock(USER_DIRECTORY, () -> FileIOHelper.loadAll(USER_DIRECTORY));
    }

    @Override
    public void delete(User user) {
        Path userFilePath = USER_DIRECTORY.resolve(user.getId().toString());

        withLock(USER_DIRECTORY, () -> FileIOHelper.delete(userFilePath));
    }

    @Override
    public boolean existsById(UUID id) {
        Path userFilePath = USER_DIRECTORY.resolve(id.toString());

        return withLock(USER_DIRECTORY, () -> FileIOHelper.exists(userFilePath));
    }

    @Override
    public boolean existsByUsername(String username) {
        return withLock(USER_DIRECTORY, () -> FileIOHelper.<User>loadAll(USER_DIRECTORY).stream()
                .anyMatch(user -> user.getUsername().equals(username)));
    }

    @Override
    public boolean existsByEmail(String email) {
        return withLock(USER_DIRECTORY, () -> FileIOHelper.<User>loadAll(USER_DIRECTORY).stream()
                .anyMatch(user -> user.getEmail().equals(email)));
    }
}
