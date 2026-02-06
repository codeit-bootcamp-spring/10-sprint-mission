package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.userStatus.UserStatusCreateDto;
import com.sprint.mission.discodeit.dto.userStatus.UserStatusResponseDto;
import com.sprint.mission.discodeit.dto.userStatus.UserStatusUpdateDto;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatusResponseDto create(UserStatusCreateDto dto);
    UserStatusResponseDto findUserStatus(UUID id);
    List<UserStatusResponseDto>  findAll();
    UserStatusResponseDto update(UserStatusUpdateDto dto);
    void delete(UUID id);
}
