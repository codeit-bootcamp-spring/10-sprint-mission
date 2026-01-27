package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
<<<<<<< HEAD
    Channel createChannel(String name, String description);
    Channel findById(UUID id);
    List<Channel> findAll();
    void updateChannel(UUID id, String name, String description);
    void delete(UUID id);

=======
    Channel createChannel(String channelName);

    void deleteChannel(UUID id);

    Channel findChannelByChannelId(UUID id);

    List<Channel> findAllChannels();
    List<Channel> findChannelByUserId(UUID userID);

    Channel updateChannel(UUID id, String channelName);

    void joinChannel(UUID userID, UUID channelID);

    void leaveChannel(UUID userID, UUID channelID);
>>>>>>> 3a7b55e457e0d55f5042c220079e6b60cb0acc7f
}
