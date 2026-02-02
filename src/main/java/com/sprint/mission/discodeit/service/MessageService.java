package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.MessageDto;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageDto.response createMessage(MessageDto.createRequest messageReq,
                                      List<BinaryContentDto.createRequest> contentReqs);
    MessageDto.response findMessage(UUID uuid);
    List<MessageDto.response> findAllByChannelId(UUID channelId);
    MessageDto.response updateMessage(UUID uuid, MessageDto.updateRequest messageReq);
    void deleteMessage(UUID uuid);
}
