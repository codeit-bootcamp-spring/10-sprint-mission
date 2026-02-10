package com.sprint.mission.discodeit.message.service;

import com.sprint.mission.discodeit.message.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.message.dto.ReadStatusResponse;
import com.sprint.mission.discodeit.message.dto.ReadStatusUpdateRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface ReadStatusService {
    ReadStatusResponse create(ReadStatusCreateRequest request);
    ReadStatusResponse find(UUID id);
    List<ReadStatusResponse> findAllByUserId(UUID userId);
    ReadStatusResponse update(ReadStatusUpdateRequest request);
    void delete(UUID id);
}
