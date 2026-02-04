package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.exception.ChannelNotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(
        prefix = "discodeit.repository",
        name = "type",
        havingValue = "jcf",
        matchIfMissing = true
)
public class JcfChannelRepository implements ChannelRepository {

    private final Map<UUID, Channel> channels = new LinkedHashMap<>();

    public void reset() {
        channels.clear();
    }

    @Override
    public synchronized Channel saveChannel(Channel channel) {
        channels.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public synchronized UUID createChannel(Channel channel) {
        channels.put(channel.getId(), channel);
        return channel.getId();
    }

    @Override
    public synchronized Channel findChannel(UUID channelId) {
        Channel channel = channels.get(channelId);
        if (channel == null) throw new ChannelNotFoundException();
        return channel;
    }

    @Override
    public synchronized List<Channel> findAllChannel() {
        return new ArrayList<>(channels.values());
    }

    @Override
    public synchronized void deleteChannel(UUID id) {
        if (channels.remove(id) == null) throw new ChannelNotFoundException();
    }
}
