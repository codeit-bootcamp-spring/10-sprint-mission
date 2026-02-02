package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.*;

public class JCFChannelRepository implements ChannelRepository {
    private Map<UUID, Channel> data;

    public JCFChannelRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public Channel save(Channel channel) {
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID channelId) {
        return Optional.ofNullable(data.get(channelId));
    }

    @Override
    public List<Channel> findAll() {
        return data.values().stream().toList();
    }

    @Override
    public boolean existsByChannelName(String channelName) {
        // 비공개 채널의 channelName이 Null이기 때문에 이렇게 진행
        return data.values().stream()
                .map(Channel::getChannelName)          // 각 채널의 name을 꺼내고
                .filter(Objects::nonNull)              // null은 제거
                .anyMatch(name -> name.equals(channelName));
    }

    @Override
    public void deleteById(UUID channelId) {
        data.remove(channelId);
    }
}
