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

    // 외부에서 객체를 받는 것 보다는 메소드 내부에서 객체 생성해서 반환
    @Override
    public Channel createChannel(String channelName) {
        if (channelName == null) {
            throw new IllegalArgumentException("생성하고자하는 채널의 채널명이 null일 수 없음");
        }
        Channel channel = new Channel(channelName);
        channels.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
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
    public Channel updateById(UUID id, String newChannelName) {
        if (newChannelName == null) {
            throw new IllegalStateException("업데이트하고자 하는 채널의 채널명이 null일 수 없음");
        }
        Channel targetChannel = findById(id);
        targetChannel.setChannelName(newChannelName);
        return targetChannel;
    }

    // 채널 삭제시 유저, 메시지 ??
    @Override
    public void deleteById(UUID id) {
        findById(id);
        channels.remove(id);
    }
}
