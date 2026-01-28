package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.*;

public class JCFChannelRepository implements ChannelRepository {
    private static ChannelRepository instance;
    private final List<Channel> data;

    private JCFChannelRepository() {
        this.data = new ArrayList<>();
    }

    public static ChannelRepository getInstance() {
        if (instance == null) instance = new JCFChannelRepository();
        return instance;
    }

    @Override
    public Channel save(Channel channel) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getId().equals(channel.getId())) {
                data.set(i, channel);
                return channel;
            }
        }

        data.add(channel);
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID channelId) {
        return data.stream()
            .filter(ch -> ch.getId().equals(channelId))
            .findFirst();
    }

    @Override
    public List<Channel> findAll() {
        return this.data;
    }

    @Override
    public void delete(UUID channelId) {
        if (!data.removeIf(user -> user.getId().equals(channelId)))
            throw new NoSuchElementException("id가 " + channelId + "인 채널은 존재하지 않습니다.");
    }
}
