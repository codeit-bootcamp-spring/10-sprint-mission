package com.sprint.mission.repository.file;

import com.sprint.mission.entity.User;
import com.sprint.mission.repository.UserRepository;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class FileUserRepository extends BaseFileRepository implements UserRepository {
    public FileUserRepository(Path directory) {
        super(directory);
    }

    @Override
    public User save(User user) {
        super.save(getFilePath(user.getId()), user);
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(load(User::getId).get(id));
    }

    @Override
    public List<User> findByChannelId(UUID channelId) {
        return load(User::getId).values().stream()
                .filter(user ->
                        user.getChannels().stream()
                                .anyMatch(channel -> channel.getId().equals(channelId)))
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findAll() {
        return List.copyOf(load(User::getId).values());
    }

    @Override
    public void deleteById(UUID userId) {
        super.delete(getFilePath(userId));
    }

    private Path getFilePath(UUID userId) {
        return super.directory.resolve(userId.toString().concat(".ser"));
    }
}
