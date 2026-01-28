package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.UUID;

public interface UserStatusRepository {
    UserStatus find(UUID userID);
    List<Boolean> findAll();
    void deleteUserStatus(UUID userID);
    UserStatus save(UserStatus userStatus);

}
