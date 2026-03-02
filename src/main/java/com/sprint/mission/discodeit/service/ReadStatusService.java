package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {

    ReadStatus createReadStatus(ReadStatusCreateRequest request);

    ReadStatus findReadStatusByReadStatusId(UUID readStatusId);

    List<ReadStatus> findAllReadStatusesByUserId(UUID userId);

    ReadStatus updateReadStatus(UUID readStatusId, ReadStatusUpdateRequest request);

    void deleteReadStatus(UUID readStatusId);
}
