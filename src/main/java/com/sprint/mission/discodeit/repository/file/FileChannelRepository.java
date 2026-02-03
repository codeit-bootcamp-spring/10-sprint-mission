package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelVisibility;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(
        name = "discodeit.repository.type",
        havingValue = "file"
)
public class FileChannelRepository extends BaseFileRepository<Channel> implements ChannelRepository {
    public FileChannelRepository(@Value("${discodeit.repository.file-directory}") String directory) {
        super(directory + "/channels.ser");
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
        return loadData().values().stream().toList();
    }

    @Override
    public List<Channel> findAllPublic() {
        return loadData().values().stream()
                .filter(channel -> channel.getVisibility() == ChannelVisibility.PUBLIC)
                .toList();
    }

    @Override
    public void deleteById(UUID id){
        Map<UUID, Channel> data = loadData();
        data.remove(id);
        saveData(data);
    }
}
