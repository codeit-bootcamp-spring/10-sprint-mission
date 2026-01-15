package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel createChannel (String name, String intro);
    Channel findChannelById(UUID channelId);
    List<Channel> findAllChannels();
    Channel updateChannel(UUID channelId, String name, String intro);
    void deleteChannel(UUID channelId);
    void enter(Channel channel, User user);
    void exit(Channel channel, User user);
    void addMessage(Channel channel, Message message);
    void removeMessage(Channel channel, Message message);
    int getCurrentUserCount(Channel channel);
    int getMessageCount(Channel channel);
}
