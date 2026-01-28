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
    public Channel find(UUID channelID) {
        return channelData.stream()
                .filter(channel -> channel.getId().equals(channelID))
                .findFirst()
                .orElseThrow(()-> new IllegalArgumentException("Channel not found: "+ channelID));
    }

    @Override
    public List<Channel> findAll() {
        return channelData;
    }

    @Override
    public void deleteChannel(UUID channelID) {
        channelData.removeIf(ch -> ch.getId().equals(channelID));
    }

    @Override
    public Channel save(Channel channel){
        channelData.removeIf(ch -> ch.getId().equals(channel.getId()));
        channelData.add(channel);
        return channel;
    }
}
