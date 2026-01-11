package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;

import java.util.UUID;

public interface MessageService {
    Message createMessage(String content, UUID channelId, UUID userId);
}
