package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.messagedto.CreateRequestDTO;
import com.sprint.mission.discodeit.dto.messagedto.MessageUpdateRequestDto;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message create(CreateRequestDTO req);
    Message find(UUID messageId);
    List<Message> findallByChannelId(UUID channelId);
    Message update(MessageUpdateRequestDto req);
    void delete(UUID messageId);
}
