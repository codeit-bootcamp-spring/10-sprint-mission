package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ReadStatusDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusService {
    ReadStatusDto.response createReadStatus(ReadStatusDto.createRequest dto);
    ReadStatusDto.response findById(UUID uuid);
    List<ReadStatusDto.response> findAllByUserId(UUID userId);
    ReadStatusDto.response updateReadStatus(ReadStatusDto.updateRequest dto);
    void deleteReadStatusById(UUID uuid);
}
