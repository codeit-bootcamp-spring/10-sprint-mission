package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    private final MessageService messageService;
    private final Map<UUID, Channel> channels = new HashMap<>();

    public JCFChannelService(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public Channel createChannel(String channelName) {
        Channel channel = new Channel(channelName);
        channels.put(channel.getId(), channel);

        return channel;
    }

    @Override
    public List<Channel> getChannelList() {
        return channels.values().stream().toList();
    }

    @Override
    public void updateChannelName(UUID channelId, String newChannelName) {
        Objects.requireNonNull(channelId, "channelId는 null일 수 없습니다.");

        Channel channel = channels.get(channelId);
        if (channel == null) {
            throw new NoSuchElementException("해당 id를 가진 채널이 존재하지 않습니다");
        } else if (channel.getChannelName().equals(newChannelName)) {
            throw new IllegalArgumentException("해당 채널의 이름이 바꿀 이름과 동일합니다.");
        }

        channel.updateChannelName(newChannelName);
    }

    @Override
    public void joinChannel(UUID channelId, User user) {
        Channel channel = channels.get(channelId);
        Objects.requireNonNull(channelId, "channelId는 null일 수 없습니다.");
        Objects.requireNonNull(user, "user값은 null일 수 없습니다.");

        if (channel == null) {
            throw new NoSuchElementException("해당 id를 가진 채널이 존재하지 않습니다.");
        }

        channel.addUser(user);
        user.updateJoinedChannels(channel);
    }

    @Override
    public void leaveChannel(UUID channelId, User user) {
        Channel channel = channels.get(channelId);
        try {
            channel.removeUser(user.getId());
            user.removeChannel(channel);
        } catch (NullPointerException e) {
            System.out.println("해당 채널이 존재하지 않습니다.");
        }
    }

    @Override
    public void deleteChannel(UUID channelId) {
        Channel channel = channels.get(channelId);

        if (channel == null) {
            throw new NoSuchElementException("해당 id를 가진 채널이 존재하지 않습니다.");
        }

        messageService.clearChannelMessage(channelId);
        channels.remove(channelId);
    }
}
