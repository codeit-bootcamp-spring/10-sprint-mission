package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.utils.FileIOHelper;
import com.sprint.mission.discodeit.utils.FileLockProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileUserStatusRepository extends FileRepositoryLockSupport implements UserStatusRepository {

    private static final Path USER_STATUS_DIRECTORY =
            FileIOHelper.resolveDirectory("userStatuses");

    public FileUserStatusRepository(FileLockProvider fileLockProvider) {
        super(fileLockProvider);
    }

    @Override
    public void save(UserStatus userStatus) {
        Path path = USER_STATUS_DIRECTORY.resolve(userStatus.getId().toString());

        withLock(USER_STATUS_DIRECTORY, () -> FileIOHelper.save(path, userStatus));
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return withLock(USER_STATUS_DIRECTORY, () -> FileIOHelper.<UserStatus>loadAll(USER_STATUS_DIRECTORY).stream()
                .filter(status -> status.getUserId().equals(userId))
                .findFirst());
    }

    @Override
    public List<UserStatus> findAll() {
        return withLock(USER_STATUS_DIRECTORY, () -> FileIOHelper.loadAll(USER_STATUS_DIRECTORY));
    }

    @Override
    public void deleteByUserId(UUID userId) {
        findByUserId(userId).ifPresent(status -> {
            Path path = USER_STATUS_DIRECTORY.resolve(status.getId().toString());
            withLock(USER_STATUS_DIRECTORY, () -> FileIOHelper.delete(path));
        });
    }

    @Override
    public boolean existsByUserId(UUID userId) {
        return withLock(USER_STATUS_DIRECTORY, () -> FileIOHelper.<UserStatus>loadAll(USER_STATUS_DIRECTORY).stream()
                .anyMatch(status -> status.getUserId().equals(userId)));
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        Path path = USER_STATUS_DIRECTORY.resolve(id.toString());
        return withLock(USER_STATUS_DIRECTORY, () -> FileIOHelper.load(path));
    }

    @Override
    public void deleteById(UUID userStatusId) {
        Path path = USER_STATUS_DIRECTORY.resolve(userStatusId.toString());
        withLock(USER_STATUS_DIRECTORY, () -> FileIOHelper.delete(path));
    }
}
