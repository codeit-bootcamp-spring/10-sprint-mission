package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;
import java.util.UUID;

public interface MessageService extends BaseService<Message, UUID> {
    void create(UUID userId, UUID channelId, String content);
}
