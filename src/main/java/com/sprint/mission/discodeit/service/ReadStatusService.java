package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatus;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {

  ReadStatus create(ReadStatusCreateRequest request);

  ReadStatus findById(UUID id);

  List<ReadStatus> findAllByUserId(UUID userId);

  ReadStatus update(UUID id, ReadStatusUpdateRequest request);

  void deleteById(UUID id);
}
