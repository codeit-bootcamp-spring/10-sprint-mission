package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.listener.ChannelLifecycleListener;

import java.util.*;

public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final List<ChannelLifecycleListener> listeners = new ArrayList<>();

    public BasicChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    public void addListener(ChannelLifecycleListener listener) {
        listeners.add(listener);
    }

    @Override
    public Channel createChannel(String name, User owner) {
        if (channelRepository.existsByName(name)) {
            throw new IllegalArgumentException("이미 존재하는 채널 이름입니다. (channelName: " + name + ")");
        }

        Channel channel = new Channel(name, owner);
        return channelRepository.save(channel);
    }

    @Override
    public Channel findChannelById(UUID channelId) {
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("해당 id의 채널이 존재하지 않습니다. (channelId: " + channelId + ")"));
    }

    @Override
    public List<Channel> findAllChannels() {
        return channelRepository.findAll();
    }

    @Override
    public Channel updateChannel(UUID channelId, String newChannelName) {
        Channel channel = findChannelById(channelId);

        if (!channel.getChannelName().equals(newChannelName) && channelRepository.existsByName(newChannelName)) {
            throw new IllegalArgumentException("이미 존재하는 채널 이름입니다. (channelName: " + newChannelName + ")");
        }

        channel.updateChannelName(newChannelName);
        return channelRepository.save(channel);
    }

    @Override
    public void deleteChannel(UUID channelId) {
        findChannelById(channelId); // 존재 확인

        // 리스너 알림
        for (ChannelLifecycleListener listener : listeners) {
            listener.onChannelDelete(channelId);
        }

        channelRepository.delete(channelId);
        System.out.println("채널 삭제 완료 (Basic).");
    }

    @Override
    public void deleteChannelsByOwnerId(UUID ownerId) {
        List<Channel> myChannels = channelRepository.findAllByOwnerId(ownerId);
        for (Channel ch : myChannels) {
            deleteChannel(ch.getId());
        }
    }
}