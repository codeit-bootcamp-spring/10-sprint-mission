package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.DuplicationReadStatusException;
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
        if (!file.exists()) return new HashMap<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, ReadStatus>) ois.readObject();
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

    // 초기화(원하면 유지)
    public void resetFile() {
        saveReadStatusFile(new LinkedHashMap<>());
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        Map<UUID, ReadStatus> map = loadReadStatusFile();

        // 중복 체크
        boolean duplicate = map.values().stream().anyMatch(existing ->
                existing.getUserId().equals(readStatus.getUserId())
                        && existing.getChannelId().equals(readStatus.getChannelId())
                        && !existing.getId().equals(readStatus.getId())
        );

        if (duplicate) {
            throw new DuplicationReadStatusException();
        }

        map.put(readStatus.getId(), readStatus);
        saveReadStatusFile(map);
        return readStatus;
    }

    @Override
    public ReadStatus findById(UUID id) {
        return loadReadStatusFile().get(id);
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return loadReadStatusFile().values().stream()
                .filter(rs -> rs.getUserId().equals(userId))
                .toList();
    }

    @Override
    public ReadStatus findByUserIdAndChannelId(UUID userId, UUID channelId) {
        return loadReadStatusFile().values().stream()
                .filter(rs -> rs.getUserId().equals(userId) && rs.getChannelId().equals(channelId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void delete(UUID id) {
        Map<UUID, ReadStatus> map = loadReadStatusFile();
        map.remove(id);
        saveReadStatusFile(map);
    }

    @Override
    public void deleteByChannelId(UUID channelId) {
        Map<UUID, ReadStatus> map = loadReadStatusFile();
        map.values().removeIf(rs -> rs.getChannelId().equals(channelId));
        saveReadStatusFile(map);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        Map<UUID, ReadStatus> map = loadReadStatusFile();
        map.values().removeIf(rs -> rs.getUserId().equals(userId));
        saveReadStatusFile(map);
    }
}
