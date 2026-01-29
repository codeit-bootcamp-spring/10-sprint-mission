package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.utils.FileIOHelper;
import org.springframework.stereotype.Repository;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class FileUserStatusRepository implements UserStatusRepository {
    private static final Path USER_STATUS_DIRECTORY =
            FileIOHelper.resolveDirectory("userStatuses");

    @Override
    public void save(UserStatus userStatus) {
        Path path = USER_STATUS_DIRECTORY.resolve(userStatus.getId().toString());

        FileIOHelper.save(path, userStatus);
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return FileIOHelper.<Optional<UserStatus>>loadAll(USER_STATUS_DIRECTORY).stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(status -> status.getUserId().equals(userId))
                .findFirst();
    }

    @Override
    public List<UserStatus> findAll() {
        return FileIOHelper.loadAll(USER_STATUS_DIRECTORY);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        findByUserId(userId).ifPresent(status -> {
            Path path = USER_STATUS_DIRECTORY.resolve(status.getId().toString());
            FileIOHelper.delete(path);
        });
    }
}
