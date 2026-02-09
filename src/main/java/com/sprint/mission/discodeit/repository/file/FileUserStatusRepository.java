package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.IOException;
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
    public FileUserStatusRepository() throws IOException {
        super(Paths.get(System.getProperty("user.dir"), "file-data-map", "UserStatus"),
                ".us");
    }

    @Override
    public UserStatus save(UserStatus userStatus) throws IOException {
        return save(userStatus, UserStatus::getId);
    }

    @Override
    public List<UserStatus> findAll() throws IOException {
        return streamAll(Stream::toList);
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) throws IOException {
        return filter(userStatus -> userStatus.matchUserId(userId)).findFirst();
    }

    // todo: remove later
    @Override
    public boolean existsByUserId(UUID userId) throws IOException {
        return anyMatch(userStatus -> userStatus.matchUserId(userId));
    }
}
