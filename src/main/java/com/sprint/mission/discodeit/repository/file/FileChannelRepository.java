package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.*;

public class FileChannelRepository implements ChannelRepository {
    private final FileStorage<Channel> data;
    public FileChannelRepository(){
        data = new FileStorage("channels");
    }
    @Override
    public Channel save(Channel channel) {
        return data.put(channel.getId(),channel);
    }

    @Override
    public Optional<Channel> update(Channel channel) {
        return Optional.ofNullable(data.put(channel.getId(),channel));
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public List<Channel> findAllByChannelType(ChannelType channelType) {
        return data.values().stream()
                .filter(c->c.getChannelType()==channelType)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        data.remove(id);
    }
}
