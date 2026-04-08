package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface MessageService {

  List<MessageDto> findAllByChannelId(UUID channelId);

  MessageDto create(MessageCreateRequest request, List<MultipartFile> attachments);

  MessageDto update(UUID id, MessageUpdateRequest request);

  void delete(UUID id);
}
