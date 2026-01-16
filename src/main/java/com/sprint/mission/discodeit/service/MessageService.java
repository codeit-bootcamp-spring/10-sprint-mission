package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.*;

public interface MessageService {

    Message create(String msg, UUID userId, UUID channelId, JCFUserService userService, JCFChannelService channelService);

    Message read(UUID id);

    List<Message> readAll();

    Message update(UUID id, String messageData);

    void delete(UUID id);

    List<Message> readUserMessageList(UUID userId, JCFUserService userService);

    List<Message> readChannelMessageList(UUID channelId, JCFChannelService channelService);
}
