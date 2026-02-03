package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.extend.FileSerDe;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Repository
public class FileReadStatusRepository extends FileSerDe<ReadStatus> implements ReadStatusRepository {
    private final String READ_STATUS_DATA_DIRECTORY = "data/readstatus";

    public FileReadStatusRepository() {
        super(ReadStatus.class);
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        return super.save(READ_STATUS_DATA_DIRECTORY, readStatus);
    }

    @Override
    public Optional<ReadStatus> findById(UUID uuid) {
        return super.load(READ_STATUS_DATA_DIRECTORY, uuid);
    }

    @Override
    public List<ReadStatus> findAll() {
        return super.loadAll(READ_STATUS_DATA_DIRECTORY);
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return findAll().stream()
                .filter(rs -> Objects.equals(rs.getUserId(), userId))
                .toList();
    }

    @Override
    public void deleteById(UUID uuid) {
        super.delete(READ_STATUS_DATA_DIRECTORY, uuid);
    }

    @Override
    public void deleteAllByChannelId(UUID channelId) {
        findAll().stream()
                .filter(rs -> Objects.equals(rs.getChannelId(), channelId))
                .forEach(rs -> deleteById(rs.getId()));
    }
}
