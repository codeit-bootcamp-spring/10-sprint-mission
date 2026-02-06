package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.*;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatusInfoDto create(UserStatusCreateDto userStatusCreateDto);

    UserStatusInfoDto findById(UUID id);

    List<UserStatusInfoDto> findAll();

    UserStatusInfoDto update(UserStatusUpdateByIdDto userStatusUpdateByIdDto);

    UserResponseDto updateByUserId(UUID userId, UserStatusUpdateByUserIdDto userStatusUpdateByUserIdDto);

    void delete(UUID id);

}
