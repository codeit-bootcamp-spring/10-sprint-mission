package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.MessageDto;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageDto.messageResponse createMessage(MessageDto.messageCreateRequest messageReq,
                                             List<BinaryContentDto.binaryContentCreateRequest> contentReqs);
    MessageDto.messageResponse findMessage(UUID uuid);
    List<MessageDto.messageResponse> findAllByChannelId(UUID channelId);
    MessageDto.messageResponse updateMessage(UUID uuid, MessageDto.messageUpdateRequest messageReq);
    void deleteMessage(UUID uuid);
}
