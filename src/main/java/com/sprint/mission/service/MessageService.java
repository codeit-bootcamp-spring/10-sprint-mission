package com.sprint.mission.service;

import com.sprint.mission.entity.Message;

import java.util.UUID;

public interface MessageService extends BaseService<Message> {
    Message create(String content, UUID userId, UUID channelId);
}
