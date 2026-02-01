package com.sprint.mission.discodeit.service;
import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    MessageDto.MessageResponse create(MessageDto.MessageRequest request, List<BinaryContentDto.BinaryContentRequest> fileInfo);
    MessageDto.MessageResponse findById(UUID messageId);
    List<MessageDto.MessageResponse> findAllByChannelId(UUID channelId);
    MessageDto.MessageResponse update(UUID id, String content);
    void delete(UUID id);
}
