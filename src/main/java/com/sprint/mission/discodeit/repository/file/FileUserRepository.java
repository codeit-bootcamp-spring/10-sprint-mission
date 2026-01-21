package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.utils.FileIOHelper;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileUserRepository implements UserRepository {
    private static final Path USER_DIRECTORY =
            FileIOHelper.resolveDirectory("users");

    @Override
    public void save(User user) {
        Path userFilePath = USER_DIRECTORY.resolve(user.getId().toString());

        FileIOHelper.save(userFilePath, user);
    }

    @Override
    public Optional<User> findById(UUID id) {
        Path userFilePath = USER_DIRECTORY.resolve(id.toString());

        return FileIOHelper.load(userFilePath);
    }

    @Override
    public List<User> findAll() {
        return FileIOHelper.loadAll(USER_DIRECTORY);
    }

    @Override
    public void delete(User user) {
        Path userFilePath = USER_DIRECTORY.resolve(user.getId().toString());

        FileIOHelper.delete(userFilePath);
    }
}
