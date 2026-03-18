package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ReadStatusService {

    ReadStatusDto createReadStatus(ReadStatusCreateRequest request);

    ReadStatusDto findReadStatusByReadStatusId(UUID readStatusId);

    List<ReadStatusDto> findAllReadStatusesByUserId(UUID userId);

    ReadStatusDto updateReadStatus(UUID readStatusId, ReadStatusUpdateRequest request);

    void deleteReadStatus(UUID readStatusId);

    void createInitialReadStatuses(
            UUID channelId,
            Set<UUID> participantIds,
            Instant lastReadAt
    );
}
