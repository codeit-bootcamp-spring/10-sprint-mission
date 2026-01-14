package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    void createChannel(Channel channel);

    void addUser(Channel channel, User user);

    void deleteUser(Channel channel, User user);

    void addMessage(Channel channel, Message message);

    void changeChannelId(Channel oldChannel, Channel newChannel);

    List<UUID> getMessageUUIDs(Channel channel);

    List<UUID> getUserUUIDs(Channel channel);

    void deleteChannel(Channel channel);

    List<Channel> getChannels();

    void deleteUserFromChannels(User user, List<UUID> channelUUIDs);

    void deleteMessage(Channel channel, Message message);
}
