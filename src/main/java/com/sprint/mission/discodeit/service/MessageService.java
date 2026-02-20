package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.MessageDto;

import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface MessageService {

  MessageDto.Response create(MessageDto.Create createRequest, List<MultipartFile> attachments);

  MessageDto.Response findById(UUID messageId);

  List<MessageDto.Response> findAllByChannelId(UUID channelId);

  MessageDto.Response update(UUID messageId, MessageDto.Update updateRequest);

  void delete(UUID messageId);
}
