package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.UUID;

public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;

    public BasicChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    @Override
    public Channel create(String name) {
        Channel newChannel = new Channel(name);
        channelRepository.save(newChannel);
        return newChannel;
    }

    @Override
    public Channel findById(UUID channelId) {
        return channelRepository.findById(channelId);
    }

    @Override
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }

    @Override
    public Channel update(UUID channelId, String name) {
        Channel channel = channelRepository.findById(channelId);
        if (channel != null) {
            channel.updateName(name);
            channelRepository.update(channel);
            return channel;
        }
        return null;
    }

    @Override
    public void delete(UUID channelId) {
        channelRepository.delete(channelId);
    }

    @Override
    public void joinChannel(UUID channelId, UUID userId) {
        Channel channel = channelRepository.findById(channelId);
        if (channel != null) {
            channelRepository.update(channel);
        }
    }

    @Override
    public void leaveChannel(UUID channelId, UUID userId) {
        Channel channel = channelRepository.findById(channelId);
        if (channel != null) {
            channelRepository.update(channel);
        }
    }
}