package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
public class JCFChannelRepository implements ChannelRepository {

    private final List<Channel> data = new ArrayList<>();

    @Override
    public Channel save(Channel channel) {
        delete(channel);
        data.add(channel);
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID channelId) {
        return data.stream()
                .filter(c -> c.getId().equals(channelId))
                .findFirst();
    }

    @Override
    public List<Channel> findAll() {
        return List.copyOf(data);
    }

    @Override
    public void delete(Channel channel) {
        data.removeIf(c -> c.equals(channel));
    }
}
