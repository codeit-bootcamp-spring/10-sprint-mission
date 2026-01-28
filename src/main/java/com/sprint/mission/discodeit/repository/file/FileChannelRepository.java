package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.util.*;

public class FileChannelRepository extends AbstractFileRepository<Channel> implements ChannelRepository {

    public FileChannelRepository(String path) {
        super(path);
    }

    @Override
    public Channel save(Channel channel) {
        Map<UUID, Channel> data = load();
        data.put(channel.getId(), channel);
        writeToFile(data);
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        Map<UUID, Channel> data = load();
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Channel> readAll() {
        Map<UUID, Channel> data = load();
        return List.copyOf(data.values());
    }

    @Override
    public void delete(UUID id) {
        Map<UUID, Channel> data = load();
        data.remove(id);
        writeToFile(data);
    }

    @Override
    public void clear() {
        writeToFile(new HashMap<UUID, Channel>());
    }
}
