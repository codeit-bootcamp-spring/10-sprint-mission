package com.sprint.mission.discodeit.user.service;

import com.sprint.mission.discodeit.user.entity.UserStatus;
import com.sprint.mission.discodeit.user.dto.UserStatusCreateRequest;
import com.sprint.mission.discodeit.user.dto.UserStatusUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    void create (UserStatusCreateRequest request );
    UserStatus find (UUID userStatusID);
    List<UserStatus> findAll( );
    void update (UserStatusUpdateRequest request);
    void updateByUserId (UUID userId);
    void delete (UUID userStatusId);
}
