package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.MessageCreateDTO;
import com.sprint.mission.discodeit.dto.message.MessageUpdateDTO;
import com.sprint.mission.discodeit.entity.Message;

import java.util.*;

public interface MessageService {

    Message create(MessageCreateDTO messageCreateDTO);

    Message findById(UUID messageId);

    List<Message> findAll();

    Message updateMessageData(MessageUpdateDTO messageUpdateDTO);

    void delete(UUID messageId);

    List<UUID> readUserMessageList(UUID userId);

    List<UUID> readChannelMessageList(UUID channelId);
}
