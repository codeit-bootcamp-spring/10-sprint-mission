package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.*;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import java.util.*;

public class JCFChannelService implements ChannelService {

    private final Map<UUID, Channel> channels = new LinkedHashMap<>();
    private final UserService userService;

    public JCFChannelService(UserService userService) {
        this.userService = userService;
    }

    public UserService getUserService() {
        return this.userService;
    }

    @Override
    public Channel createChannel(String channelName) {
        for (Channel channel : channels.values()) {
            if (channel.getChannelName().equals(channelName))
                throw new DuplicationChannelException();
        }
        Channel channel = new Channel(channelName);
        channels.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Channel findChannel(UUID channelId) {
        Channel channel = channels.get(channelId);
        if (channel == null) {
            throw new ChannelNotFoundException();
        }
        return channel;
    }

    @Override
    public List<Channel> findAllChannel() {
        return new ArrayList<>(channels.values());
    }

    @Override
    public Channel userAddChannel(UUID channelId, UUID userId) {
        Channel channel = findChannel(channelId);
        User user = userService.findUser(userId);
        if (channel.hasChannelUser(user)) throw new AlreadyJoinedChannelException();
        channel.addChannelUser(user);
        return channel;
    }

    @Override
    public Channel userRemoveChannel(UUID channelId, UUID userId) {
        Channel channel = findChannel(channelId);
        User user = userService.findUser(userId);
        if (!channel.hasChannelUser(user)) throw new UserNotInChannelException();
        channel.removeChannelUser(user);
        return channel;
    }

    @Override
    public Channel nameUpdateChannel(UUID channelId, String channelName) {
        Channel channel = findChannel(channelId);
        if (channel.getChannelName().equals(channelName)) throw new DuplicationChannelException();
        channel.updateChannelName(channelName);
        return channel;
    }

    @Override
    public Channel deleteChannel(UUID channelId) {
        Channel channel = channels.remove(channelId);
        if (channel == null) throw new ChannelNotFoundException();
        return channel;
    }

    @Override
    public Channel findByUserChannel(UUID userId) {
        for (Channel channel : channels.values()) {
            for (User member : channel.getChannelUser()) {
                if (member.getId().equals(userId)) {
                    return channel;
                }
            }
        }
        throw new ChannelNotFoundException();
    }
}
