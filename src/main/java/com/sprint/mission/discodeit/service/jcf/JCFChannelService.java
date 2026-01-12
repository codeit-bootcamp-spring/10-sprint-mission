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
    public void updateById(UUID id, String newChannelName) {
        Channel targetChannel = findById(id);
        targetChannel.setChannelName(newChannelName);
    }

    // 채널 삭제시 유저, 메시지 ??
    @Override
    public void deleteById(UUID id) {
        channels.remove(id);
    }

    @Override
    public void printAllChannels() {
        for (Channel channel : channels.values()) {
            System.out.println(channel);
        }
    }
}
