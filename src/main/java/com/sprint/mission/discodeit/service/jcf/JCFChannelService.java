package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    private final List<Channel> channelData;

    public JCFChannelService() {
        this.channelData = new ArrayList<>();
    } // List , Set ??

    @Override
    public Channel create(String name) {
        Channel channel = new Channel(name);
        channelData.add(channel);
        return channel;
    }

    @Override
    public Channel read(UUID id) {
        return channelData.stream()
                .filter(channel -> channel.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Channel> readAll(){
        return channelData;
    } // realALl이랑 read랑 type 맞춰줘야 하나?

    @Override
    public void update(UUID id, String name) {
        Channel channel = read(id);
        channel.updateName(name);
    }

    // 삭제가 잘되지 않았음
    @Override
    public void delete(UUID id) {
        Channel channel = read(id);
        channelData.remove(channel);
    }
}
