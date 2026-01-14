package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    Message create(String message, User user, Channel channel);
    Message findById(UUID messageId);
    List<Message> findAllByChannelId(UUID channelId);
    Message update(UUID id, String content);
    void delete(UUID id);
}
