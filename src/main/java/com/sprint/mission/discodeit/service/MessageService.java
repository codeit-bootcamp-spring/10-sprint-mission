package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message create(String contents, User sender, Channel channel);
    Message find(UUID messageID);
    List<Message> readAll();
    void update(UUID messageID, String contents);
    void delete(UUID messageID);
}
