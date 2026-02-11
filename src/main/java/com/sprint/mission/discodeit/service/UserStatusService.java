package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.userStatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.userStatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

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
