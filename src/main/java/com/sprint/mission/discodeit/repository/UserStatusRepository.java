package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.UUID;

public interface UserStatusRepository {
    public UserStatus save(UserStatus userStatus);
    public UserStatus deleteByID(UUID id);
    public UserStatus deleteByUserID(UUID userID);
    public UserStatus findByID(UUID id);
    public List<UserStatus> findAll();
}
