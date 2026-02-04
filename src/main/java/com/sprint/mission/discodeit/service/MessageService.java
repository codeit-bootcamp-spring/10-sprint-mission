package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.MessageCreateDTO;
import com.sprint.mission.discodeit.dto.message.MessageDTO;
import com.sprint.mission.discodeit.dto.message.MessageUpdateDTO;

import java.util.*;

public interface MessageService {

    MessageDTO create(MessageCreateDTO messageCreateDTO);

    MessageDTO findById(UUID messageId);

    List<MessageDTO> findAll();

    MessageDTO updateMessageData(MessageUpdateDTO messageUpdateDTO);

    void delete(UUID messageId);

    List<UUID> readUserMessageList(UUID userId);

    List<UUID> readChannelMessageList(UUID channelId);
}
