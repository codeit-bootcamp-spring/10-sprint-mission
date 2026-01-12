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
        channels.put(channel.getChannelId(), channel);

        return channel;
    }

    @Override
    public List<Channel> getChannelList() {
        return channels.values().stream().toList();
    }

    @Override
    public void updateChannelName(UUID channelId, String newChannelName) {
        channels.entrySet().stream()
                .filter(entry -> entry.getKey().equals(channelId))
                .findFirst()
                .ifPresent(entry -> entry.getValue().updateChannelName(newChannelName));
    }

    @Override
    public void joinChannel(UUID channelId, User user) {
        Channel channel = channels.get(channelId);
        try {
            channel.addUser(user);
            user.updateJoinedChannels(channel);
        } catch (NullPointerException e) {
            System.out.println("해당 채널이 존재하지 않습니다.");
        }
    }

    @Override
    public void leaveChannel(UUID channelId, User user) {
        Channel channel = channels.get(channelId);
        try {
            channel.removeUser(user.getUserId());
            user.removeChannel(channel);
        } catch (NullPointerException e) {
            System.out.println("해당 채널이 존재하지 않습니다.");
        }
    }

    @Override
    public void deleteChannel(UUID channelId) {
        Channel channel = channels.get(channelId);
        if (channel != null) {
            messageService.clearMessage(channelId);
            // 채널을 지우면 자동으로 내부 리스트도 제거됨
            channels.remove(channelId);
        }
    }
}
