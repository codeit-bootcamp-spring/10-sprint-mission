package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.page.PageResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface MessageService {

  MessageDto create(MessageCreateRequest createRequest, List<MultipartFile> attachments);

  MessageDto findById(UUID messageId);

  PageResponse<MessageDto> findAllByChannelId(UUID channelId, int page, int size);

  MessageDto update(UUID messageId, MessageUpdateRequest updateRequest);

  void delete(UUID messageId);
}
