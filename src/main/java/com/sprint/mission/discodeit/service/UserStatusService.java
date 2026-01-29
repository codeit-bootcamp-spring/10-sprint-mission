package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateRequestDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequestDto;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.UUID;

public interface UserStatusService {
    UserStatus create(UserStatusCreateRequestDto userStatusCreateRequestDto);
    UserStatus find(UUID Id);
    UserStatus findAll(UUID Id);
    void update(UserStatusUpdateRequestDto userStatusUpdateRequestDto);
    void delete(UUID userId);

}
