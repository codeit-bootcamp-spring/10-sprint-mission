package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.userstatus.CreateUserStatusRequestDTO;
import com.sprint.mission.discodeit.dto.userstatus.UpdateStatusByStatusIdRequestDTO;
import com.sprint.mission.discodeit.dto.userstatus.UpdateStatusByUserIdRequestDTO;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponseDTO;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatusResponseDTO createUserStatus(CreateUserStatusRequestDTO dto);

    UserStatusResponseDTO findByUserStatusId(UUID userStatusId);

    List<UserStatusResponseDTO> findAll();

    UserStatusResponseDTO updateUserStatus(UpdateStatusByStatusIdRequestDTO dto);

    UserStatusResponseDTO updateStatusByUserId(UUID userId, UpdateStatusByUserIdRequestDTO dto);

    void deleteStatus(UUID userStatusId);
}
