package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.utils.FileIOHelper;
import org.springframework.stereotype.Repository;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@Repository
public class FileReadStatusRepository implements ReadStatusRepository {

    private static final Path READ_STATUS_DIRECTORY =
            FileIOHelper.resolveDirectory("read-status");

    @Override
    public void save(ReadStatus readStatus) {
        Path filePath = READ_STATUS_DIRECTORY.resolve(
                readStatus.getId().toString()
        );

        FileIOHelper.save(filePath, readStatus);
    }

    @Override
    public List<ReadStatus> findByChannelId(UUID channelId) {
        return FileIOHelper.<ReadStatus>loadAll(READ_STATUS_DIRECTORY).stream()
                .filter(readStatus ->
                        readStatus.getChannelId().equals(channelId)
                )
                .toList();
    }

    @Override
    public void delete(ReadStatus readStatus) {
        Path filePath = READ_STATUS_DIRECTORY.resolve(
                readStatus.getId().toString()
        );

        FileIOHelper.delete(filePath);
    }
}
