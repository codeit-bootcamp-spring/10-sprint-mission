package com.sprint.mission.discodeit.service.jcf;


import com.sprint.mission.discodeit.exception.AlreadyJoinedChannelException;
import com.sprint.mission.discodeit.exception.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.DuplicationChannelException;
import com.sprint.mission.discodeit.exception.UserNotInChannelException;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {

    private final Map<UUID, Channel> channels = new LinkedHashMap<>();

    //중복 예외 한개로 통합
    private Channel getChannelOrThrow(UUID channelId) {
        Channel channel = channels.get(channelId);
        if (channel == null) {
            throw new ChannelNotFoundException("존재하지 않는 채널입니다.");
        }
        return channel;
    }

    @Override
    public Channel createChannel(String channelName) {
        for (Channel channel : channels.values()) {
            if (channel.getChannelName().equals(channelName)) {
                throw new DuplicationChannelException();
            }
        }
        Channel channel = new Channel(channelName);
        channels.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Channel findChannel(UUID channelId) {
        return getChannelOrThrow(channelId);
    }

    @Override
    public List<Channel> findAllChannel() {
        return new ArrayList<>(channels.values());
    }

    @Override
    public Channel memberAddChannel(UUID channelId, UUID userId) {
        Channel channel = getChannelOrThrow(channelId);
        if (channel.hasMember(userId)) {
            throw new AlreadyJoinedChannelException();
        }
        channel.addMember(userId);
        return channel;
    }

    @Override
    public Channel memberRemoveChannel(UUID channelId, UUID userId) {
        Channel channel = getChannelOrThrow(channelId);
        if (!channel.hasMember(userId)) {
            throw new UserNotInChannelException();
        }
        channel.removeMember(userId);
        return channel;
    }

    @Override
    public Channel deleteChannel(UUID channelId) {
        Channel channel = channels.remove(channelId);
        if (channel == null) {
            throw new ChannelNotFoundException();
        }
        return channel;
    }

    @Override
    public Channel nameUpdateChannel(UUID channelId, String channelName) {
        Channel channel = getChannelOrThrow(channelId);
        if (channel.getChannelName().equals(channelName)) {
            throw new DuplicationChannelException();
        }
        channel.updateChannelName(channelName);
        return channel;
    }
}
