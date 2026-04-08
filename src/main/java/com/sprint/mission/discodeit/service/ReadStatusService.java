package com.sprint.mission.discodeit.service;


import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import java.util.List;
import java.util.UUID;

public interface ReadStatusService {

  List<ReadStatusDto> findAllByUserId(UUID userId);

  void delete(UUID id);

  ReadStatusDto find(UUID userId, UUID channelId);

  ReadStatusDto create(ReadStatusCreateRequest request);
  ReadStatusDto update(UUID id, ReadStatusUpdateRequest request);
}
