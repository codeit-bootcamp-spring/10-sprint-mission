package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.*;

public class FileChannelRepository extends BaseFileRepository<Channel> implements ChannelRepository {
    public FileChannelRepository() {
        super("channels.ser");
    }

    @Override
    public Channel save(Channel channel){
        Map<UUID, Channel> data = loadData();
        data.put(channel.getId(), channel);
        saveData(data);
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id){
        return Optional.ofNullable(loadData().get(id));
    }

    @Override
    public List<Channel> findAll(){
        return new ArrayList<>(loadData().values());
    }

    @Override
    public void delete(Channel channel){
        Map<UUID, Channel> data = loadData();
        data.remove(channel.getId());
        saveData(data);
    }
}
