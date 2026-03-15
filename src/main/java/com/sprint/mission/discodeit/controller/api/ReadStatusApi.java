package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

public interface ReadStatusApi {

    ResponseEntity<ReadStatusDto> create(ReadStatusCreateRequest request);

    ResponseEntity<ReadStatusDto> update(UUID readStatusId, ReadStatusUpdateRequest request);

    ResponseEntity<List<ReadStatusDto>> findAllByUserId(UUID userId);
}