package com.sprint.mission.discodeit.service;
import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    Message create(MessageDto.MessageRequest request, List<BinaryContentDto.BinaryContentRequest> fileInfo);
    Message findById(UUID messageId);
    List<Message> findAllByChannelId(UUID channelId);
    Message update(UUID id, String content);
    void delete(UUID id);
    List<Message> getMessageListByChannelId(UUID channelId);
}
