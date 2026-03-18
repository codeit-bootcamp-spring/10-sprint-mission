package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface MessageService {

  MessageResponse create(MessageCreateRequest request);

  Slice<MessageResponse> findAllByChannelId(UUID channelId, Pageable pageable);

  MessageResponse update(MessageUpdateRequest request);

  void delete(UUID messageId);
}