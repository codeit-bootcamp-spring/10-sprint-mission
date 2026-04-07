package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import org.springframework.data.domain.Slice;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface MessageService {

  Message create(MessageCreateRequest messageCreateRequest,
      List<BinaryContentCreateRequest> binaryContentCreateRequests);

  Message find(UUID messageId);

  // 커서 기반 패이징
  List<Message> findAllByChannel_Id(UUID channelId, Instant cursor, int size);

  Message update(UUID messageId, MessageUpdateRequest request);

  void delete(UUID messageId);
}
