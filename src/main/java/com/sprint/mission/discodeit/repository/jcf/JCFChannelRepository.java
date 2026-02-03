package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class JCFChannelRepository implements ChannelRepository {

    private final Map<UUID, Channel> data = new HashMap<>();

    @Override
    public void save(Channel channel) {
        data.put(channel.getId(), channel);
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public void delete(Channel channel) {
        data.remove(channel.getId());
    }

    @Override
    public List<Channel> findVisibleChannels(UUID requesterId) {
        return data.values().stream()
                .filter(channel ->
                        channel.isPublic()
                                || (channel.isPrivate() && channel.hasMember(requesterId))
                )
                .toList();
    }

    @Override
    public boolean existsById(UUID channelId) {
        return data.containsKey(channelId);
    }
}
