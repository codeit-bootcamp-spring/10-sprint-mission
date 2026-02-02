package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@Profile("file")
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
    public List<Channel> findAllPublic() {
        return loadData().values().stream()
                .filter(Channel::isPublic)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public void deleteById(UUID id){
        Map<UUID, Channel> data = loadData();
        data.remove(id);
        saveData(data);
    }
}
