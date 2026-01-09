package com.sprint.mission.service.jcf;

import com.sprint.mission.entity.Channel;
import com.sprint.mission.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> channels;

    public JCFChannelService() {
        this.channels = new HashMap<>();
    }

    @Override
    public void create(String name) {
        Channel channel = new Channel(name);
        channels.put(channel.getId(), channel);
    }

    @Override
    public Channel findById(UUID id) {
        return getChannelOrThrow(id);
    }

    @Override
    public List<Channel> findAll() {
        // 리스트나 맵, 셋을 넘겨줄 땐 내부 요소 보호를 위해 얕은복사
        return new ArrayList<>(channels.values());
    }

    @Override
    public void update(UUID id, String name) {
        Channel channel = findById(id);
        validateUpdateChannelNameNotExist(id, name);
        channel.updateName(name);
    }

    @Override
    public void deleteById(UUID id) {
        validateChannelExists(id);
        channels.remove(id);
    }

    private void validateChannelExists(UUID id) {
        if (!channels.containsKey(id)) {
            throw new IllegalArgumentException("채널이 존재하지 않습니다.");
        }
    }

    private Channel getChannelOrThrow(UUID id) {
        validateChannelExists(id);
        return channels.get(id);
    }

    private void validateUpdateChannelNameNotExist(UUID id, String name) {
        String trimmedName = name.trim();
        boolean exist = channels.values().stream()
                .anyMatch(channel -> channel.getName().equals(trimmedName) &&
                        !channel.getId().equals(id));

        if (exist) {
            throw new IllegalArgumentException("존재하는 채널명입니다. 다른이름을 선택해주세요");
        }
    }
}
