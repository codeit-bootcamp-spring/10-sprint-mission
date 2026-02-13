package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.MessageDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageDto.Response create(MessageDto.CreateRequest request);
    MessageDto.Response find(UUID messageId);
    List<MessageDto.Response> findAllByChannelId(UUID channelId);
    MessageDto.Response update(UUID messageId, MessageDto.UpdateRequest request);
    void delete(UUID messageId);
}
