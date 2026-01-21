package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.*;

public class JCFChannelRepository implements ChannelRepository {
    public final Map<UUID, Channel> channelMap = new HashMap<>();

    public Channel save(Channel channel) {
        channelMap.put(channel.getId(), channel);
        return channel;
    }

    public void delete(UUID id) {
        channelMap.remove(id);
    }

    public Optional<Channel> findById(UUID id) {
        return Optional.ofNullable(channelMap.get(id));
    }

    public List<Channel> findAll(){
        return new ArrayList<>(channelMap.values());
    }
}
