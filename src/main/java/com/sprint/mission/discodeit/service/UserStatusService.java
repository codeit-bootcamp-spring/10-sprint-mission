package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.userstatus.UpdateStatusByStatusIdRequestDTO;
import com.sprint.mission.discodeit.dto.userstatus.UpdateStatusByUserIdRequestDTO;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusDto;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {

    UserStatusDto findByUserStatusId(UUID userStatusId);

    List<UserStatusDto> findAll();

    UserStatusDto updateUserStatus(UpdateStatusByStatusIdRequestDTO dto);

    UserStatusDto updateStatusByUserId(UUID userId, UpdateStatusByUserIdRequestDTO dto);

    void deleteStatus(UUID userStatusId);
}
