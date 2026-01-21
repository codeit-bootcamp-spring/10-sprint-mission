package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.utility.FileSerDe;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileUserRepository extends FileSerDe<User> implements UserRepository {
    private final String USER_DATA_DIRECTORY = "data/user";

    public FileUserRepository() {
        super(User.class);
    }

    @Override
    public User save(User user) {
        return super.save(USER_DATA_DIRECTORY, user);
    }

    @Override
    public Optional<User> findById(UUID userId) {
        return super.load(USER_DATA_DIRECTORY, userId);
    }

    @Override
    public List<User> findAll() {
        return super.loadAll(USER_DATA_DIRECTORY);
    }

    @Override
    public void deleteById(UUID userId) {
        super.delete(USER_DATA_DIRECTORY, userId);
    }
}
