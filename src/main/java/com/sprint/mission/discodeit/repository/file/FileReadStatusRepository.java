package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.lock.FileLockProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

@Repository
@ConditionalOnProperty(
    prefix = "discodeit.repository",
    name = "type",
    havingValue = "file"
)
public class FileReadStatusRepository implements ReadStatusRepository {

  private final Path filePath;
  private final FileLockProvider fileLockProvider;

  public FileReadStatusRepository(
      @Value("${discodeit.repository.file-directory:.discodeit}") String fileDirectory,
      FileLockProvider fileLockProvider
  ) {
    try {
      Files.createDirectories(Paths.get(fileDirectory));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    this.filePath = Paths.get(fileDirectory, "readStatus.dat");
    this.fileLockProvider = fileLockProvider;
  }

  @SuppressWarnings("unchecked")
  private Map<UUID, ReadStatus> loadReadStatusFile() {
    ReentrantLock lock = fileLockProvider.getLock(filePath);
    lock.lock();
    try {
      File file = filePath.toFile();
        if (!file.exists()) {
            return new LinkedHashMap<>();
        }

      try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
        Object obj = ois.readObject();
          if (obj instanceof Map<?, ?>) {
              return (Map<UUID, ReadStatus>) obj;
          }
        return new LinkedHashMap<>();
      } catch (EOFException e) {
        return new LinkedHashMap<>();
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      lock.unlock();
    }
  }

  private void saveReadStatusFile(Map<UUID, ReadStatus> map) {
    ReentrantLock lock = fileLockProvider.getLock(filePath);
    lock.lock();
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
      oos.writeObject(map);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      lock.unlock();
    }
  }

  public void resetFile() {
    saveReadStatusFile(new LinkedHashMap<>());
  }

  @Override
  public ReadStatus save(ReadStatus readStatus) {
    Map<UUID, ReadStatus> map = loadReadStatusFile();
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