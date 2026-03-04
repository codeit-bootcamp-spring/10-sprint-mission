package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
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
public class FileBinaryContentRepository implements BinaryContentRepository {

  private final Path filePath;
  private final FileLockProvider fileLockProvider;

  public FileBinaryContentRepository(
      @Value("${discodeit.repository.file-directory:.discodeit}") String fileDirectory,
      FileLockProvider fileLockProvider
  ) {
    try {
      Files.createDirectories(Paths.get(fileDirectory));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    this.filePath = Paths.get(fileDirectory, "binaryContent.dat");
    this.fileLockProvider = fileLockProvider;
  }

  @SuppressWarnings("unchecked")
  private Map<UUID, BinaryContent> loadBinaryFile() {
    ReentrantLock lock = fileLockProvider.getLock(filePath);
    lock.lock();
    try {
      File file = filePath.toFile();
        if (!file.exists()) {
            return new HashMap<>();
        }

      try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
        return (Map<UUID, BinaryContent>) ois.readObject();
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      lock.unlock();
    }
  }

  private void saveBinaryFile(Map<UUID, BinaryContent> map) {
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
    saveBinaryFile(new LinkedHashMap<>());
  }

  @Override
  public BinaryContent save(BinaryContent content) {
    Map<UUID, BinaryContent> map = loadBinaryFile();
    map.put(content.getId(), content);
    saveBinaryFile(map);
    return content;
  }

  @Override
  public BinaryContent findById(UUID id) {
    return loadBinaryFile().get(id);
  }

  @Override
  public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
      if (ids == null || ids.isEmpty()) {
          return List.of();
      }

    Map<UUID, BinaryContent> map = loadBinaryFile();
    return ids.stream()
        .map(map::get)
        .filter(Objects::nonNull)
        .toList();
  }

  @Override
  public void delete(UUID id) {
      if (id == null) {
          return;
      }

    Map<UUID, BinaryContent> map = loadBinaryFile();
    map.remove(id);
    saveBinaryFile(map);
  }
}