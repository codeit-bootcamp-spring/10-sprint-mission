package com.sprint.mission.discodeit.channel.repository;

import com.sprint.mission.discodeit.channel.Channel;
import com.sprint.mission.discodeit.common.ChannelType;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(prefix = "discodeit.repository", name = "type", havingValue = "jcf", matchIfMissing = true)
public class JCFChannelRepository implements ChannelRepository {
    private final List<Channel> data = new ArrayList<>();

    @Override
    public Optional<Channel> findById(UUID channelId) {
        return data.stream()
                .filter(c -> c.getId().equals(channelId))
                .findFirst();
    }

    @Override
    public Optional<Channel> findByName(String channelName) {
        return data.stream()
                .filter(c -> c.getChannelType() == ChannelType.PUBLIC
                        && c.getChannelName().equals(channelName))
                .findFirst();
    }

    @Override
    public List<Channel> findAll() {
        return List.copyOf(data);
    }

    @Override
    public List<Channel> findAllByUserId(UUID userId) {
        return data.stream()
                .filter(c -> c.getUserIds()
                        .stream()
                        .anyMatch(findUserId -> findUserId.equals(userId)))
                .toList();

    }

    @Override
    public void save(Channel channel) {
        if(data.contains(channel))
            data.set(data.indexOf(channel), channel);
        else
            data.add(channel);
    }

    @Override
    public void deleteById(UUID channelId) {
        data.removeIf(c -> c.getId().equals(channelId));
    }
}
