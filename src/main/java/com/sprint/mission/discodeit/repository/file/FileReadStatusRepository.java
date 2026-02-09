package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

public class FileReadStatusRepository extends FileDomainRepository<ReadStatus> implements ReadStatusRepository {
    public FileReadStatusRepository() throws IOException {
        super(Paths.get(System.getProperty("user.dir"), "file-data-map", "ReadStatus"),
                ".rs");
    }

    @Override
    public ReadStatus save(ReadStatus entity) throws IOException {
        return save(entity, ReadStatus::getId);
    }

    @Override
    public boolean existsByUserAndChannelId(UUID userId, UUID channelId) throws IOException {
        return filter(status -> status.matchUserId(userId))
                .anyMatch(status -> status.matchChannelId(channelId));
    }
}
