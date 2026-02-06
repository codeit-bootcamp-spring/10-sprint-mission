package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequestDTO;
import com.sprint.mission.discodeit.dto.response.UserStatusResponseDTO;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequestDTO;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatusResponseDTO create(UserStatusCreateRequestDTO userStatusCreateRequestDTO);
    UserStatusResponseDTO find(UUID userStatusId);
    List<UserStatusResponseDTO> findAll();
    UserStatusResponseDTO update(UUID userStatusId, UserStatusUpdateRequestDTO userStatusUpdateRequestDTO);
    UserStatusResponseDTO updateByUserId(UUID userId, UserStatusUpdateRequestDTO userStatusUpdateRequestDTO);
    void delete(UUID userStatusId);
}
