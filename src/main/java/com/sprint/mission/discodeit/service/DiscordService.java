package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface DiscordService {
    //UserService
    List<User> getUsersByChannel(UUID channelId);

    // ChannelService
    List<Channel> getChannelsByUser(UUID userId);

    // MessageService
    List<Message> getMessagesByUser(UUID userId);
    List<Message> getMessagesByChannel(UUID channelId);

    void deleteUser(UUID userId);
    void deleteChannel(UUID channelId);


}
