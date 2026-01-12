package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> channels;

    public JCFChannelService() {
        channels = new HashMap<>();
    }
    @Override
    public void createChannel(Channel channel) {
        if (channel == null) {
            throw new IllegalArgumentException("생성하고자 하는 메시지가 null일 수 없음");
        }
        channels.put(channel.getId(), channel);
    }

    @Override
    public Channel findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("찾고자 하는 채널의 id가 null일 수 없음");
        }
        Channel channel = channels.get(id);
        if (channel == null) {
            throw new IllegalStateException("해당 id의 채널을 찾을 수 없음");
        }
        return channel;
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(channels.values());
    }

    @Override
    public void updateById(UUID id, String newChannelName) {
        Channel targetChannel = findById(id);
        if (newChannelName == null) {
            throw new IllegalStateException("업데이트하고자 하는 채널의 채널명이 null일 수 없음");
        }
        targetChannel.setChannelName(newChannelName);
    }

    // 채널 삭제시 유저, 메시지 ??
    @Override
    public void deleteById(UUID id) {
        findById(id);
        channels.remove(id);
    }

    @Override
    public void printAllChannels() {
        for (Channel channel : channels.values()) {
            System.out.println(channel);
        }
    }
}
