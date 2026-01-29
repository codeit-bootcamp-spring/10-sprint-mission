package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface ChannelService {
<<<<<<< HEAD
    Channel create(ChannelType type, String name, String description);
    Channel find(UUID channelId);
    List<Channel> findAll();
    Channel update(UUID channelId, String newName, String newDescription);
    void delete(UUID channelId);
=======
    Channel createChannel(String name, String description);
    Channel findById(UUID id);
    List<Channel> findAll();
    void updateChannel(UUID id, String name, String description);
    void delete(UUID id);
>>>>>>> upstream/김혜성
}
