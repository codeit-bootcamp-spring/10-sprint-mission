package com.sprint.mission.discodeit.service;


import com.sprint.mission.discodeit.dto.userstatusdto.UserStatusRequestDTO;
import com.sprint.mission.discodeit.dto.userstatusdto.UserStatusResponseDTO;
import com.sprint.mission.discodeit.dto.userstatusdto.UserStatusUpdateRequestDTO;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {

    public UserStatusResponseDTO create(UserStatusRequestDTO req);

    public UserStatus find(UUID id);

    public List<UserStatusResponseDTO> findAll();

    public UserStatusResponseDTO update(UserStatusRequestDTO req);

    public UserStatusResponseDTO updateByUserId(UUID userId);

    public UserStatusResponseDTO activateUserOnline(UUID userId, UserStatusUpdateRequestDTO req);

    public void delete(UUID id);


}
