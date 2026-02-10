package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ReadStatus.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatus.response.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.ReadStatus.request.ReadStatusUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    ReadStatusResponse create(ReadStatusCreateRequest request);
    ReadStatusResponse find(UUID readStatusID);
    List<ReadStatusResponse> findAllByUserID(UUID userID);
    ReadStatusResponse update(UUID readStatusId, ReadStatusUpdateRequest request);
    void delete(UUID readStatusID);
}
