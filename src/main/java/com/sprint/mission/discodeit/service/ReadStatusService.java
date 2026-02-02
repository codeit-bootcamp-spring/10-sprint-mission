package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ReadStatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatus.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.ReadStatus.ReadStatusUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    ReadStatusResponse create(ReadStatusCreateRequest request);
    ReadStatusResponse find(UUID readStatusID);
    List<ReadStatusResponse> findAllByUserID(UUID userID);
    ReadStatusResponse update(ReadStatusUpdateRequest request);
    void delete(UUID readStatusID);
}
