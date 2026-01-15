package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    // Setter
    void setMessageService(MessageService messageService);
    void setChannelService(ChannelService channelService);
    // CRUD
    User create(String name);
    User find(UUID id);
    List<User> findAll();
    User updateName(UUID userID, String name);
    void deleteUser(UUID userID);

    List<String> findJoinedChannels(UUID userID);
}
