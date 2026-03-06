package com.sprint.mission.discodeit.service;


import com.sprint.mission.discodeit.dto.userstatusdto.UserStatusRequestDTO;
import com.sprint.mission.discodeit.dto.userstatusdto.UserStatusDto;
import com.sprint.mission.discodeit.dto.userstatusdto.UserStatusUpdateRequestDTO;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {

//    public UserStatusDto create(UserStatusRequestDTO req);

    public UserStatus find(UUID id);

    public List<UserStatusDto> findAll();


    public UserStatusDto activateUserOnline(UUID userId, UserStatusUpdateRequestDTO req);

    public void delete(UUID id);

    public boolean isUserOnline(UUID userId);


}
