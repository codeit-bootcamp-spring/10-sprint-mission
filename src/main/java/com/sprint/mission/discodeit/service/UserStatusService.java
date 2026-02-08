package com.sprint.mission.discodeit.service;


import com.sprint.mission.discodeit.dto.userstatusdto.UserStateRequestDTO;
import com.sprint.mission.discodeit.dto.userstatusdto.UserStateResponseDTO;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    public UserStateResponseDTO create(UserStateRequestDTO req);

    public UserStatus find(UUID id);

    public List<UserStateResponseDTO> findAll();

    public UserStateResponseDTO update(UserStateRequestDTO req);

    public UserStateResponseDTO updateByUserId(UUID userId);

    public UserStateResponseDTO activateUserOnline(UUID userId);

    public void delete(UUID id);




}
