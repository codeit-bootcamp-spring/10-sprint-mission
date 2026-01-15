package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {

    private final Map<UUID, Channel> channels;    // 유저 전체 목록

    public JCFChannelService() {
        this.channels = new HashMap<>();
    }

    @Override
    public Channel create(String name, String description) {
        Objects.requireNonNull(name, "채널 이름은 필수 항목입니다.");
        Objects.requireNonNull(description, "채널 설명은 필수 항목입니다.");

        Channel channel = new Channel(name, description);
        channels.put(channel.getId(), channel);

        return channel;
    }

    public void delete(UUID channelId) {
        Objects.requireNonNull(channelId, "채널 Id가 유효하지 않습니다.");

        if (!channels.containsKey(channelId)) {
            System.out.println("삭제하려는 채널이 존재하지 않습니다.");
            return;
        }
        channels.remove(channelId);
    }

    @Override
    public Channel findById(UUID id) {
        Objects.requireNonNull(id, "채널 Id가 유효하지 않습니다.");
        return Objects.requireNonNull(channels.get(id), "Id에 해당하는 채널이 존재하지 않습니다.");

    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(channels.values());
    }

    @Override
    public Channel update(UUID channelId, String name, String description) {
        Objects.requireNonNull(channelId, "채널 Id가 유효하지 않습니다.");
        Channel channel = findById(channelId);
        if (channel == null) {
            System.out.println("수정하려는 채널이 존재하지 않습니다.");
            return null;
        }

        if (name != null) channel.updateName(name);
        if (description != null) channel.updateDescription(description);
        return channel;
    }

    @Override
    public List<Message> getMessageList(UUID channelId) {
        Objects.requireNonNull(channelId, "채널 Id가 유효하지 않습니다.");
        Channel channel = findById(channelId);
        if (channel == null) {
            System.out.println("메세지를 출력하려는 채널이 존재하지 않습니다.");
            return List.of();
        }
        return channel.getMessages();
    }
}
