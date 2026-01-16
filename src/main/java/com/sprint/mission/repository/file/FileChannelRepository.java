package com.sprint.mission.repository.file;

import com.sprint.mission.entity.Channel;
import com.sprint.mission.repository.ChannelRepository;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class FileChannelRepository extends BaseFileRepository implements ChannelRepository {
    public FileChannelRepository(Path directory) {
        super(directory);
    }

    @Override
    public Channel save(Channel channel) {
        super.save(getFilePath(channel.getId()), channel);
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return Optional.ofNullable(load(Channel::getId).get(id));
    }

    @Override
    public List<Channel> findByUserId(UUID userId) {
        return load(Channel::getId).values().stream()
                .filter(channel ->
                        channel.getUsers().stream()
                                .anyMatch(user ->
                                        user.getId().equals(userId)))
                .collect(Collectors.toList());
    }

    @Override
    public List<Channel> findAll() {
        return List.copyOf(load(Channel::getId).values());
    }

    @Override
    public void deleteById(UUID channelId) {
        super.delete(getFilePath(channelId));
    }

    private Path getFilePath(UUID channelId) {
        return super.directory.resolve(channelId.toString().concat(".ser"));
    }
}
