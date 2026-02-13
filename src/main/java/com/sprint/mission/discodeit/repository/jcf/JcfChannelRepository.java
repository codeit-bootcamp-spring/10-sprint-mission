package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
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

    @Override
    public synchronized UUID createChannel(Channel channel) {
        if (channel == null) throw new IllegalArgumentException("channel is null");
        // 신규 생성용이지만, 실제 저장은 saveChannel에 위임
        saveChannel(channel);
        return channel.getId();
    }

    @Override
    public synchronized Channel saveChannel(Channel channel) {
        if (channel == null) throw new IllegalArgumentException("channel is null");
        channels.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public synchronized Channel findChannel(UUID channelId) {
        if (channelId == null) return null;
        return channels.get(channelId);
    }

    @Override
    public synchronized List<Channel> findAllChannel() {
        return new ArrayList<>(channels.values());
    }

    @Override
    public synchronized void deleteChannel(UUID channelId) {
        if (channelId == null) return;
        channels.remove(channelId); // 없으면 그냥 무시
    }
}
