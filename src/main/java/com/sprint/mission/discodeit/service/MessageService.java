package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface MessageService {

  MessageResponse create(MessageCreateRequest createRequest, List<MultipartFile> attachments);

  MessageResponse findById(UUID messageId);

  List<MessageResponse> findAllByChannelId(UUID channelId);

  MessageResponse update(UUID messageId, MessageUpdateRequest updateRequest);

  void delete(UUID messageId);
}
