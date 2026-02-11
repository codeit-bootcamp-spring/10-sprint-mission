package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    // creat
    ReadStatus createReadStatus(ReadStatusCreateRequest request);

    // read
    ReadStatus findReadStatusById(UUID readStatusId);

    // all read
    List<ReadStatus> findAllByUserId(UUID userId);

    // update
    ReadStatus updateReadStatus(ReadStatusUpdateRequest request);

    // delete
    void deleteReadStatus(UUID readStatusId);
}
