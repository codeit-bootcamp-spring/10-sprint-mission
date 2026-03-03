package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Repository
@ConditionalOnProperty(
        prefix = "discodeit.repository",
        name = "type",
        havingValue = "file"
)
public class FileUserStatusRepository extends FileDomainRepository<UserStatus> implements UserStatusRepository {
    public FileUserStatusRepository() {
        super(Paths.get(System.getProperty("user.dir"), "file-data-map", "UserStatus"),
                ".us");
    }

    @Override
    public UserStatus save(UserStatus userStatus) {
        return save(userStatus, UserStatus::getId);
    }

    @Override
    public List<UserStatus> findAll() {
        return streamAll(Stream::toList);
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return filter(userStatus -> userStatus.matchUserId(userId)).findFirst();
    }

    @Override
    public boolean existsByUserId(UUID userId) {
        return anyMatch(userStatus -> userStatus.matchUserId(userId));
    }
}
