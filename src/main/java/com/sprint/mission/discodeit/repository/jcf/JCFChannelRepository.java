package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFChannelRepository implements ChannelRepository {

    private final List<Channel> data = new ArrayList<>();

    @Override
    public Channel save(Channel channel) {
        delete(channel);
        data.add(channel);
        return channel;
    }

    @Override
    public Channel findChannelById(UUID channelId) {
        return data.stream()
                .filter(c -> c.getId().equals(channelId))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));
    }

    @Override
    public List<Channel> findAllChannel() {
        return new ArrayList<>(data);
    }

    @Override
    public void delete(Channel channel) {
        data.removeIf(c -> c.equals(channel));
    }
}
