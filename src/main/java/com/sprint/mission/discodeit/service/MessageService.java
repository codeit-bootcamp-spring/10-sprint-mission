package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import java.util.UUID;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;


public interface MessageService {

  MessageDto create(MessageCreateRequest request, List<MultipartFile> attachments);

  MessageDto findById(UUID id);

  List<MessageDto> findAllByChannelId(UUID channelId);

  MessageDto update(UUID id, MessageUpdateRequest request);

  void deleteById(UUID id);
}