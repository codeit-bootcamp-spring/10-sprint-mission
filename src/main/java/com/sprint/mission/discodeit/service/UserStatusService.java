package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateRequestDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequestDto;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatus create(UserStatusCreateRequestDto userStatusCreateRequestDto);
    UserStatus find(UUID Id);
    List<UserStatus> findAll(UUID Id);
    UserStatus update(UserStatusUpdateRequestDto userStatusUpdateRequestDto);
    UserStatus updateByUserId(UserStatusUpdateRequestDto userStatusUpdateRequestDto);
    void delete(UUID id);

}
