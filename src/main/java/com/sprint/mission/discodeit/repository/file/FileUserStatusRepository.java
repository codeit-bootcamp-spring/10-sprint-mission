package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.extend.FileSerDe;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Repository
public class FileUserStatusRepository extends FileSerDe<UserStatus> implements UserStatusRepository {
    private final String USER_STATUS_DATA_DIRECTORY = "data/userstatus";

    public FileUserStatusRepository() {
        super(UserStatus.class);
    }

    @Override
    public UserStatus save(UserStatus userStatus) {
        return super.save(USER_STATUS_DATA_DIRECTORY, userStatus);
    }

    @Override
    public Optional<UserStatus> findById(UUID uuid) {
        return super.load(USER_STATUS_DATA_DIRECTORY, uuid);
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return findAll().stream()
                .filter(us -> Objects.equals(us.getUserId(), userId))
                .findFirst();
    }

    @Override
    public List<UserStatus> findAll() {
        return super.loadAll(USER_STATUS_DATA_DIRECTORY);
    }

    @Override
    public void deleteById(UUID uuid) {
        super.delete(USER_STATUS_DATA_DIRECTORY, uuid);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        findAll().stream()
                .filter(us -> Objects.equals(us.getUserId(), userId))
                .findFirst()
                .ifPresent(us -> deleteById(us.getId()));
    }
}
