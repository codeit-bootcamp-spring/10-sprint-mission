package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequestDto;
import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    public ReadStatus create(ReadStatusCreateRequestDto readStatusCreateRequestDto);
    public ReadStatus find(UUID id);
    public List<ReadStatus> findAllByUserId(UUID userId);
    public ReadStatus update();
    public void delete(UUID id);
}
