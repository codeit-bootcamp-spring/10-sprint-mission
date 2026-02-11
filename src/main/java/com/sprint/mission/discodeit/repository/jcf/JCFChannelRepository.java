package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelVisibility;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(
        name = "discodeit.repository.type",
        havingValue = "jcf",
        matchIfMissing = true
)
public class JCFChannelRepository implements ChannelRepository {
    private final Map<UUID, Channel> data = new HashMap<>();

    @Override
    public Channel save(Channel channel){
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id){
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Channel> findAll(){
        return data.values().stream().toList();
    }

    // PUBLIC 채널 전부 조회
    @Override
    public List<Channel> findAllPublic() {
        return data.values().stream()
                .filter(channel -> channel.getVisibility() == ChannelVisibility.PUBLIC)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        data.remove(id);
    }
}
