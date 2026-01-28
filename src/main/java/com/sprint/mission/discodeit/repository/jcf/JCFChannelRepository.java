package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFChannelRepository implements ChannelRepository {
    private final Map<UUID, Channel> channelList;
    private final UserService userService;

    public JCFChannelRepository(UserService userService){
        this.userService = userService;
        channelList = new HashMap<>();
    }

    @Override
    public Channel save(Channel channel) {
        channelList.put(channel.getId(),channel);
        return channel;
    }

    @Override
    public Channel findById(UUID channelId) {
        Channel channel = channelList.get(channelId);
        if(channel == null){
            throw new IllegalArgumentException("해당 채널을 찾을 수 없습니다");
        }
        return channel;
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(channelList.values());
    }

    @Override
    public void deleteById(UUID channelId) {
        channelList.remove(channelId);
    }
}
