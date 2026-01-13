package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User create(String name);
    User find(UUID id);
    List<User> findAll();
    User updateName(UUID id, String name);
    void delete(UUID id);

    List<String> findJoinedChannels(UUID userID);
}
