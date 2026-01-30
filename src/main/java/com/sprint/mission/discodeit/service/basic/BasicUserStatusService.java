package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.userStatus.UserStatusCreateRequestDTO;
import com.sprint.mission.discodeit.dto.request.userStatus.UserStatusUpdateRequestDTO;
import com.sprint.mission.discodeit.dto.response.UserStatusResponseDTO;
import com.sprint.mission.discodeit.service.UserStatusService;

import java.util.List;
import java.util.UUID;

public class BasicUserStatusService implements UserStatusService {
    @Override
    public UserStatusResponseDTO create(UserStatusCreateRequestDTO userStatusCreateRequestDTO) {
        return null;
    }

    @Override
    public UserStatusResponseDTO findById(UUID id) {
        return null;
    }

    @Override
    public List<UserStatusResponseDTO> findAll() {
        return List.of();
    }

    @Override
    public UserStatusResponseDTO updateByUserId(UserStatusUpdateRequestDTO userStatusUpdateRequestDTO) {
        return null;
    }

    @Override
    public UserStatusResponseDTO delete(UUID id) {
        return null;
    }
}
