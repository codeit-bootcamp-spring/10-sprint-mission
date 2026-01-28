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
    void enter(UUID userId, UUID channelId);
    void exit(UUID userId, UUID channelId );
    void addMessage(UUID channelId, UUID messageId);
    void removeMessage(UUID channelId, UUID messageId);
    int getCurrentUserCount(UUID channelId);
    int getMessageCount(UUID channelId);
}
