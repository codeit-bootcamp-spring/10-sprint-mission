package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {

    private final Map<UUID, Channel> channels;    // 유저 전체 목록

    public JCFChannelService() {
        this.channels = new HashMap<>();
    }


    @Override
    public void addUser(UUID userId, UUID channelId) {  // 참여 사용자 추가
        Channel channel = findById(channelId);
        if (channel != null) {
            channel.addUser(userId);
        }
    }

    @Override
    public void removeUser(UUID channelId, UUID userId) {// 참여 사용자 제거
        Channel channel = findById(channelId);
        if (channel != null)
            channel.removeUser(userId);
    }

    @Override
    public void create(String name, String description) {
        Channel channel = new Channel(name, description);
        channels.put(channel.getId(), channel);
    }

    @Override
    public Channel findById(UUID id) {
        return channels.get(id);
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(channels.values());
    }

    @Override
    public void update(UUID id, String name, String description) {
        Channel channel = findById(id);
        if (channel != null)
            channel.update(name, description);
    }

    @Override
    public void delete(UUID id) {
        channels.remove(id);
    }

}
