package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    public ReadStatusResponse create(ReadStatusCreateRequest request);
    public ReadStatusResponse findById(UUID readStatusId);
    public List<ReadStatusResponse> findAllByUserId(UUID userId);
    public ReadStatusResponse update(ReadStatusUpdateRequest request);
    public void delete(UUID readStatusId);
    public void deleteAllByChannelId(UUID channelId);
}
