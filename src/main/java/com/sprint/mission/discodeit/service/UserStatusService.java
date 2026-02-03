package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserStatusCreateDto;
import com.sprint.mission.discodeit.dto.UserStatusResponseDto;
import com.sprint.mission.discodeit.dto.UserStatusUpdateDto;

import java.util.UUID;

public interface UserStatusService {
    UserStatusResponseDto create(UserStatusCreateDto dto);
    UserStatusResponseDto find(UUID id);
    UserStatusResponseDto findByUserId(UUID userId);
    UserStatusResponseDto update(UserStatusUpdateDto dto);
    void delete(UUID id);
}
