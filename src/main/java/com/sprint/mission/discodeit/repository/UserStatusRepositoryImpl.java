package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class UserStatusRepositoryImpl implements UserStatusRepository{
    @Override
    public UserStatus save(UserStatus userStatus) {
        return null;
    }

    @Override
    public UserStatus findById(UUID userStatusId) {
        return null;
    }

    @Override
    public UserStatus findByUserId(UUID userId) {
        return null;
    }

    @Override
    public List<UserStatus> findAll() {
        return List.of();
    }

    @Override
    public void delete(UserStatus userStatus) {

    }
}
