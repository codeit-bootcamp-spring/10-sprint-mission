package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.readstatus.CreateReadStatusRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.readstatus.UpdateReadStatusRequest;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {

    UUID createReadStatus(CreateReadStatusRequest request);

    ReadStatusResponse findReadStatusByReadStatusId(UUID readStatusId);

    List<ReadStatusResponse> findAllReadStatusesByUserId(UUID userId);

    ReadStatusResponse updateReadStatus(UpdateReadStatusRequest request);

    void deleteReadStatus(UUID readStatusId);
}
