package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import java.util.List;
import java.util.UUID;

public interface ReadStatusService {

  ReadStatusResponse create(ReadStatusCreateRequest createRequest);

  ReadStatusResponse findById(UUID readStatusId);

  List<ReadStatusResponse> findAllByUserId(UUID userId);

  ReadStatusResponse update(UUID readStatusId, ReadStatusUpdateRequest updateRequest);

  void delete(UUID readStatusId);
}
