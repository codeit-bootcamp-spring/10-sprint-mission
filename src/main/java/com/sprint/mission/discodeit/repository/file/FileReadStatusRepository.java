package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.nio.file.Paths;
import java.util.UUID;

@Repository
@ConditionalOnProperty(
        prefix = "discodeit.repository",
        name = "type",
        havingValue = "file"
)
public class FileReadStatusRepository extends FileDomainRepository<ReadStatus> implements ReadStatusRepository {
    public FileReadStatusRepository() {
        super(Paths.get(System.getProperty("user.dir"), "file-data-map", "ReadStatus"),
                ".rs");
    }

    @Override
    public ReadStatus save(ReadStatus entity) {
        return save(entity, ReadStatus::getId);
    }

    @Override
    public boolean existsByUserAndChannelId(UUID userId, UUID channelId) {
        return filter(status -> status.matchUserId(userId))
                .anyMatch(status -> status.matchChannelId(channelId));
    }
}
