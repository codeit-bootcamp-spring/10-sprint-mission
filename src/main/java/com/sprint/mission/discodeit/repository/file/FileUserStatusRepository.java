package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

import java.util.Optional;
import java.util.UUID;

public class FileUserStatusRepository implements UserStatusRepository {
    @Override
    public Optional<UserStatus> findById(UUID id) {
        return Optional.empty();
    }

    @Override
    public void save(UUID id, UserStatus userStatus) {

    }

    @Override
    public void deleteByUserId(UUID userId) {

    }
}
