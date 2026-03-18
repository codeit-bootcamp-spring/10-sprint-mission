package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;

import com.sprint.mission.discodeit.entity.ReadStatus;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ReadStatusService {

  ReadStatus create(UUID userId, UUID channelId, Instant lastReadAt);

  ReadStatus findById(UUID id);

  List<ReadStatus> findAllByUserId(UUID userId);

  ReadStatus update(UUID id, Instant newLastReadAt);

  void deleteById(UUID id);
}
