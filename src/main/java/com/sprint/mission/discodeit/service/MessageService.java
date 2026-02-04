package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageDto.Response create(MessageDto.CreateRequest request);
    List<MessageDto.Response> findAllByChannelId(UUID channelId);
//    List<Message> findAll();
    MessageDto.Response update(MessageDto.UpdateRequest request);
    void delete(UUID messageId);
}
