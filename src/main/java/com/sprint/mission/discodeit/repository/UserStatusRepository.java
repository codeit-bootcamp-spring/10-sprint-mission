package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.UUID;

public interface UserStatusRepository {
    public void save(UserStatus userStatus);
    public UserStatus deleteByID(UUID id);
    public UserStatus findByID(UUID id);
    public List<UserStatus> findAll();
}
