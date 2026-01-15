package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;

import java.util.*;

public interface UserService {

    User create(String name);

    User read(UUID id);

    List<User> readAll();

    User update(UUID id, String name);

    void delete(UUID id);

    List<User> readChannelUserList(UUID channelId, JCFChannelService channelService);
}