package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
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
public class FileUserStatusRepository implements UserStatusRepository {

    private final Path filePath;

    public FileUserStatusRepository(
            @Value("${discodeit.repository.file-directory:.discodeit}") String fileDirectory
    ) {
        try {
            Files.createDirectories(Paths.get(fileDirectory));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.filePath = Paths.get(fileDirectory, "userStatus.dat");
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, UserStatus> loadUserStatusFile() {
        File file = filePath.toFile();
        if (!file.exists()) return new HashMap<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, UserStatus>) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void saveUserStatusFile(Map<UUID, UserStatus> map) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
            oos.writeObject(map);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 초기화(원하면 유지)
    public void resetFile() {
        saveUserStatusFile(new LinkedHashMap<>());
    }

    @Override
    public UserStatus save(UserStatus status) {
        Map<UUID, UserStatus> map = loadUserStatusFile();
        map.put(status.getId(), status);
        saveUserStatusFile(map);
        return status;
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        return Optional.ofNullable(loadUserStatusFile().get(id));
    }

    @Override
    public List<UserStatus> findAll() {
        return new ArrayList<>(loadUserStatusFile().values());
    }

    @Override
    public UserStatus findByUserId(UUID userId) {
        return loadUserStatusFile().values().stream()
                .filter(status -> status.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void delete(UUID id) {
        Map<UUID, UserStatus> map = loadUserStatusFile();
        map.remove(id);
        saveUserStatusFile(map);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        Map<UUID, UserStatus> map = loadUserStatusFile();
        map.values().removeIf(status -> status.getUserId().equals(userId));
        saveUserStatusFile(map);
    }
}
