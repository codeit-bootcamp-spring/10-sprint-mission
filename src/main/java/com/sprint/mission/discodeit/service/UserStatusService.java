package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserStatusCreateDto;
import com.sprint.mission.discodeit.dto.UserStatusInfoDto;
import com.sprint.mission.discodeit.dto.UserStatusUpdateByIdDto;
import com.sprint.mission.discodeit.dto.UserStatusUpdateByUserIdDto;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatusInfoDto create(UserStatusCreateDto userStatusCreateDto);

    UserStatusInfoDto findById(UUID id);

    List<UserStatusInfoDto> findAll();

    UserStatusInfoDto update(UserStatusUpdateByIdDto userStatusUpdateByIdDto);

    UserStatusInfoDto updateByUserId(UserStatusUpdateByUserIdDto userStatusUpdateByUserIdDto);

    void delete(UUID id);

}
