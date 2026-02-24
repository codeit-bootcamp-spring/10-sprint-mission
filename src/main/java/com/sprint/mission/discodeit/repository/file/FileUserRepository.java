package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
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
public class FileUserRepository implements UserRepository {

  private final Path filePath;
  private final FileLockProvider fileLockProvider;

  public FileUserRepository(
      @Value("${discodeit.repository.file-directory.discodeit}") String fileDirectory,
      FileLockProvider fileLockProvider
  ) {
    try {
      Files.createDirectories(Paths.get(fileDirectory));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    this.filePath = Paths.get(fileDirectory, "users.dat");
    this.fileLockProvider = fileLockProvider;
  }

  @SuppressWarnings("unchecked")
  private Map<UUID, User> loadUserFile() {
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
          return (Map<UUID, User>) obj;
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

  private void saveUserFile(Map<UUID, User> users) {
    ReentrantLock lock = fileLockProvider.getLock(filePath);
    lock.lock();
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
      oos.writeObject(users);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      lock.unlock();
    }
  }

  public void resetFile() {
    saveUserFile(new LinkedHashMap<>());
  }

  @Override
  public User save(User user) {
    Map<UUID, User> users = loadUserFile();
    users.put(user.getId(), user);
    saveUserFile(users);
    return user;
  }

  @Override
  public User findById(UUID id) {
    return loadUserFile().get(id);
  }

  @Override
  public List<User> findAll() {
    return new ArrayList<>(loadUserFile().values());
  }

  @Override
  public void delete(UUID id) {
    Map<UUID, User> users = loadUserFile();
    users.remove(id);
    saveUserFile(users);
  }

  @Override
  public boolean existsByEmail(String email) {
    return loadUserFile().values().stream()
        .anyMatch(user -> user.getEmail().equals(email));
  }

  @Override
  public boolean existsByName(String name) {
    return loadUserFile().values().stream()
        .anyMatch(user -> user.getName().equals(name));
  }


  @Override
  public User findByName(String name) {
    if (name == null) {
      return null;
    }
    return loadUserFile().values().stream()
        .filter(u -> name.equals(u.getName()))
        .findFirst()
        .orElse(null);
  }
}