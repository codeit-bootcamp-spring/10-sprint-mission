package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserStatus.UserStatusRequestCreateDto;
import com.sprint.mission.discodeit.dto.UserStatus.UserStatusRequestUpdateDto;
import com.sprint.mission.discodeit.dto.UserStatus.UserStatusResponseDto;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatusResponseDto create(UserStatusRequestCreateDto request);

    UserStatusResponseDto find(UUID id);

    List<UserStatusResponseDto> findAll();

    void update(UserStatusRequestUpdateDto request);

    void updateByUserId(UUID id);

    void delete(UUID id);
}
