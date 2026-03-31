package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import java.time.Instant;
import java.util.UUID;

public interface MessageService {

  MessageDto create(MessageCreateRequest req);

  PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant cursor, int size);

  MessageDto update(MessageUpdateRequest req);

  void delete(UUID messageId);
}