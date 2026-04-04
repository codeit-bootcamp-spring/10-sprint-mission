package com.sprint.mission.discodeit.service;


import com.sprint.mission.discodeit.dto.userstatusdto.UserStatusDto;
import com.sprint.mission.discodeit.dto.userstatusdto.UserStatusUpdateRequestDTO;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.UUID;

public interface UserStatusService {


    UserStatusDto find(UUID id);

    UserStatusDto activateUserOnline(UUID userId, UserStatusUpdateRequestDTO req);

    void delete(UUID id);


}
