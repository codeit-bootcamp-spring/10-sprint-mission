package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.utils.FileIOHelper;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
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

    @Override
    public boolean existsByUserIdAndChannelId(UUID userId, UUID channelId) {
        return FileIOHelper.<ReadStatus>loadAll(READ_STATUS_DIRECTORY).stream()
                .anyMatch(readStatus ->
                        readStatus.getUserId().equals(userId)
                                && readStatus.getChannelId().equals(channelId)
                );
    }

    @Override
    public Optional<ReadStatus> findById(UUID readStatusId) {
        Path filePath = READ_STATUS_DIRECTORY.resolve(readStatusId.toString());
        return FileIOHelper.load(filePath);
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return FileIOHelper.<ReadStatus>loadAll(READ_STATUS_DIRECTORY).stream()
                .filter(readStatus ->
                        readStatus.getUserId().equals(userId)
                )
                .toList();
    }

    @Override
    public void deleteById(UUID readStatusId) {
        Path filePath = READ_STATUS_DIRECTORY.resolve(readStatusId.toString());
        FileIOHelper.delete(filePath);
    }
}
