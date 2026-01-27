package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class FileChannelRepository implements ChannelRepository {
    private final Map<UUID, Channel> data;
    private final FileObjectStore fileObjectStore;


    public FileChannelRepository(FileObjectStore fileObjectStore) {
        this.data = fileObjectStore.getChannelsData();
        this.fileObjectStore = fileObjectStore;
    }

    @Override
    public void save(Channel channel) {
        data.put(channel.getId(), channel);
        fileObjectStore.saveData();
    }

    @Override
    public Optional<Channel> findById(UUID channelId) {
        return Optional.ofNullable(data.get(channelId));
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void delete(UUID channelId) {
        data.remove(channelId);
        fileObjectStore.saveData();
    }
}
