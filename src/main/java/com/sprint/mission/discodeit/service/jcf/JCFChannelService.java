package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data;

    // 생성자
    public JCFChannelService() {
        this.data = new HashMap<>();
    }
    public JCFChannelService(Channel channel) {
        this.data = new HashMap<>();
        data.put(channel.getId(), channel);
    }

    @Override
    public void create() {
        Channel channel = new Channel();
        this.data.put(channel.getId(), channel);
    }

    @Override
    public Optional<Channel> read(UUID id) {
        if (data.isEmpty() || !data.containsKey(id)) {
            System.out.println("해당 채널이 존재하지 않습니다.");
        }
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public Optional<ArrayList<Channel>> readAll() {
        ArrayList<Channel> list = new ArrayList<>(data.values());
        if (data.isEmpty()) {
            System.out.println("채널 데이터가 존재하지 않습니다.");
        }
        return Optional.ofNullable(list);
    }

    @Override
    public void update(Channel channelData) {
        this.data.put(channelData.getId(), channelData);
    }

    @Override
    public Channel delete(UUID id) {
        return data.remove(id);
    }
}
