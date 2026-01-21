package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFChannelRepository implements ChannelRepository {
    // field
    private final List<Channel> channelData;

    public JCFChannelRepository() {
        channelData = new ArrayList<>();
    }


    @Override
    public Optional<Channel> find(UUID channelID) {
        return channelData.stream()
                .filter(channel -> channel.getId().equals(channelID))
                .findFirst();
    }

    @Override
    public List<Channel> findAll() {
        return channelData;
    }

    @Override
    public void deleteChannel(Channel channel) {
        channelData.remove(channel);
    }

    @Override
    public Channel save(Channel channel){
        channelData.removeIf(ch -> ch.getId().equals(channel.getId()));
        channelData.add(channel);
        return channel;
    }
}
