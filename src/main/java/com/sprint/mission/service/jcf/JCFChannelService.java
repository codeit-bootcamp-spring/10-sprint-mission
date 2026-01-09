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
        String trimmedName = trimmedName(name);
        validateChannelNameNotExists(trimmedName);
        Channel channel = new Channel(trimmedName);
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
        validateUpdateChannelNameNotExist(name);
        Channel channel = getChannelOrThrow(id);
        String trimmedName = trimmedName(name);
        channel.updateName(trimmedName);
    }

    @Override
    public void deleteById(UUID id) {
        Channel channel = getChannelOrThrow(id);
        channels.remove(channel.getId());
    }

    private void validateChannelNameNotExists(String name) {
        boolean existChannelName = channels.values().stream()
                .anyMatch(channel -> channel.getName().equals(name));

        if (existChannelName) {
            throw new IllegalArgumentException("이미 존재하는 채널명입니다.");
        }
    }

    private Channel getChannelOrThrow(UUID id) {
        if (!channels.containsKey(id)) {
            throw new IllegalArgumentException("해당 채널이 존재하지 않습니다");
        }
        return channels.get(id);
    }

    private void validateUpdateChannelNameNotExist(String name) {
        boolean exist = channels.values().stream()
                .anyMatch(channel -> channel.getName().equals(name));

        if (exist) {
            throw new IllegalArgumentException("존재하는 채널명입니다. 다른이름을 선택해주세요");
        }
    }

    private String trimmedName(String name){
        return name.trim();
    }
}
