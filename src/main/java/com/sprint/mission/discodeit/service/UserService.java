package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.*;

public interface UserService {

    User create(String name);

    User read(UUID id);

    List<User> readAll();

    User update(UUID id, String name);

    void delete(UUID id);

    void joinToChannel(UUID userId, UUID channelId);

    void quitFormChannel(UUID userId, UUID channelId);

    List<UUID> readUserChannelList(UUID id);

    List<UUID> readUserMessageList(UUID id);
}