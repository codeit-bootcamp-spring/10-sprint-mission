package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.DuplicationReadStatusException;
import com.sprint.mission.discodeit.exception.StatusNotFoundException;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Repository
@ConditionalOnProperty(
        prefix = "discodeit.repository",
        name = "type",
        havingValue = "file"
)
public class FileReadStatusRepository implements ReadStatusRepository {

    private final Path filePath;

    public FileReadStatusRepository(
            @Value("${discodeit.repository.file-directory:.discodeit}") String fileDirectory
    ) {
        try {
            Files.createDirectories(Paths.get(fileDirectory));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.filePath = Paths.get(fileDirectory, "readStatus.dat");
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, ReadStatus> loadReadStatusFile() {
        File file = filePath.toFile();
        if (!file.exists()) return new LinkedHashMap<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof Map<?, ?>) return (Map<UUID, ReadStatus>) obj;
            return new LinkedHashMap<>();
        } catch (EOFException e) {
            return new LinkedHashMap<>();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void saveReadStatusFile(Map<UUID, ReadStatus> map) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
            oos.writeObject(map);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void resetFile() {
        saveReadStatusFile(new LinkedHashMap<>());
    }

    @Override
    public synchronized ReadStatus save(ReadStatus readStatus) {
        Map<UUID, ReadStatus> map = loadReadStatusFile();

        boolean duplicate = map.values().stream().anyMatch(existing ->
                existing.getUserId().equals(readStatus.getUserId())
                        && existing.getChannelId().equals(readStatus.getChannelId())
                        && !existing.getId().equals(readStatus.getId())
        );

        if (duplicate) throw new DuplicationReadStatusException();

        map.put(readStatus.getId(), readStatus);
        saveReadStatusFile(map);
        return readStatus;
    }

    @Override
    public synchronized ReadStatus findById(UUID id) {
        return loadReadStatusFile().get(id);
    }

    @Override
    public synchronized List<ReadStatus> findAllByUserId(UUID userId) {
        return loadReadStatusFile().values().stream()
                .filter(rs -> rs.getUserId().equals(userId))
                .toList();
    }

    @Override
    public synchronized ReadStatus findByUserIdAndChannelId(UUID userId, UUID channelId) {
        return loadReadStatusFile().values().stream()
                .filter(rs -> rs.getUserId().equals(userId) && rs.getChannelId().equals(channelId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public synchronized void delete(UUID id) {
        Map<UUID, ReadStatus> map = loadReadStatusFile();
        ReadStatus removed = map.remove(id);
        if (removed == null) throw new StatusNotFoundException();
        saveReadStatusFile(map);
    }

    @Override
    public synchronized void deleteByChannelId(UUID channelId) {
        Map<UUID, ReadStatus> map = loadReadStatusFile();
        map.values().removeIf(rs -> rs.getChannelId().equals(channelId));
        saveReadStatusFile(map);
    }

    @Override
    public synchronized void deleteByUserId(UUID userId) {
        Map<UUID, ReadStatus> map = loadReadStatusFile();
        map.values().removeIf(rs -> rs.getUserId().equals(userId));
        saveReadStatusFile(map);
    }
}
