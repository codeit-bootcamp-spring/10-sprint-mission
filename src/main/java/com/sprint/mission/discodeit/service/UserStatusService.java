package com.sprint.mission.discodeit.service;


import com.sprint.mission.discodeit.dto.userstatusdto.USRequestDTO;
import com.sprint.mission.discodeit.dto.userstatusdto.USResponseDTO;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    public USResponseDTO create(USRequestDTO req);

    public UserStatus find(UUID id);

    public List<USResponseDTO> findAll();

    public USResponseDTO update(USRequestDTO req);

    public USResponseDTO updateByUserId(UUID userId);

    public void delete(UUID id);




}
