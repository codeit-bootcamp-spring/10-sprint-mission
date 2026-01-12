package com.sprint.mission.discodeit.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data;

    public JCFChannelService() {
        this.data = new HashMap<>();
    }

    @Override
    public Channel create(Channel channel) {
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Channel read(UUID id) {
        if(!data.containsKey(id)){
            throw new NoSuchElementException("조회 실패 : 해당 ID의 채널을 찾을 수 없습니다.");
        }
        return data.get(id);
    }

    @Override
    public List<Channel> readAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Channel update(Channel channel) {
        if (!data.containsKey(channel.getId())) {
            throw new NoSuchElementException("수정 실패 : 존재하지 않는 채널 ID입니다.");
        }
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public void delete(UUID id) {
        if (!data.containsKey(id)) {
            throw new NoSuchElementException("삭제 실패 : 존재하지 않는 채널 ID입니다.");
        }
        data.remove(id);
    }

}
