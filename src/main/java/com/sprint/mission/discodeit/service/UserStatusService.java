package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateRequestDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponseDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequestDto;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatusResponseDto create(UserStatusCreateRequestDto userStatusCreateRequestDto);
    UserStatusResponseDto find(UUID Id);
    List<UserStatusResponseDto> findAll(UUID Id);
    UserStatusResponseDto update(UserStatusUpdateRequestDto userStatusUpdateRequestDto);
    UserStatusResponseDto updateByUserId(UserStatusUpdateRequestDto userStatusUpdateRequestDto);
    void delete(UUID id);

}
