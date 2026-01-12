package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;


public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data;
    public JCFChannelService() {
        this.data = new HashMap<>();
    }

    @Override
    public Channel createChannel(String name, String type) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("채널 이름은 필수입니다.");
        if (type == null || type.isBlank()) throw new IllegalArgumentException("채널 타입은 필수입니다.");
        Channel channel = new Channel(name, type);
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Channel getChannel(UUID id) {
        validateChannelId(id);
        return data.get(id);
    }

    @Override
    public List<Channel> getAllChannels() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Channel updateChannel(UUID id, String name, String type) {
        validateChannelId(id);
        Channel channel = data.get(id);
        Optional.ofNullable(name).ifPresent(channel::updateName);
        Optional.ofNullable(type).ifPresent(channel::updateType);
        return channel;
    }

    @Override
    public void deleteChannel(UUID id) {
        validateChannelId(id);
        data.remove(id);
    }

    @Override
    public void validateChannel(Channel channel) {
        if (channel == null || channel.getId() == null) {
            throw new IllegalArgumentException("채널 정보가 없습니다.");
        }
        if (data.get(channel.getId()) == null) {
            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        }
    }
    private void validateChannelId(UUID id){
        if (id == null) throw new IllegalArgumentException("채널 ID가 없습니다.");
        if (!data.containsKey(id)) throw new IllegalArgumentException("존재하지 않는 채널입니다.");
    }
}
