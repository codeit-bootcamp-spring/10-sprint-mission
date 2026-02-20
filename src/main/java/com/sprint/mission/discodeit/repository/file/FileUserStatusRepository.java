package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileUserStatusRepository implements UserStatusRepository {

  private final Path dirPath;
  private final FileLockProvider fileLockProvider;

  public FileUserStatusRepository(
      @Value("${discodeit.repository.file-directory}") String dir,
      FileLockProvider fileLockProvider
  ) {
    this.dirPath = Paths.get(dir, "userstatuses");
    this.fileLockProvider = fileLockProvider;
    init();
  }

  private void init() {
    if (!Files.exists(dirPath)) {
      try {
        Files.createDirectories(dirPath);
      } catch (IOException e) {
        throw new RuntimeException("UserStatus 데이터 폴더 생성 실패", e);
      }
    }
  }

  @Override
  public UserStatus save(UserStatus userStatus) {
    writeToFile(userStatus);
    return userStatus;
  }

  @Override
  public Optional<UserStatus> findById(UUID userStatusId) {
    Path path = dirPath.resolve(userStatusId + ".ser");
    ReentrantLock lock = fileLockProvider.getLock(path);
    lock.lock();
    try {
      if (!Files.exists(path)) {
        return Optional.empty();
      }
      try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(path))) {
        return Optional.ofNullable((UserStatus) ois.readObject());
      } catch (IOException | ClassNotFoundException e) {
        throw new RuntimeException("UserStatus 데이터 조회 실패", e);
      }
    } finally {
      lock.unlock();
    }
  }

  @Override
  public Optional<UserStatus> findByUserId(UUID userId) {
    return findAll().stream()
        .filter(userStatus -> userStatus.getUserId().equals(userId))
        .findFirst();
  }

  @Override
  public List<UserStatus> findAll() {
    if (!Files.exists(dirPath)) {
      return List.of();
    }
    try (Stream<Path> stream = Files.list(dirPath)) {
      return stream
          .map(path -> {
            ReentrantLock lock = fileLockProvider.getLock(path);
            lock.lock();
            try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(path))) {
              return (UserStatus) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
              throw new RuntimeException("UserStatus 데이터 조회 실패", e);
            } finally {
              lock.unlock();
            }
          }).toList();
    } catch (IOException e) {
      throw new RuntimeException("UserStatus 데이터 목록 조회 실패", e);
    }
  }

  @Override
  public void delete(UserStatus userStatus) {
    Path path = dirPath.resolve(userStatus.getId() + ".ser");
    ReentrantLock lock = fileLockProvider.getLock(path);
    lock.lock();
    try {
      Files.deleteIfExists(path);
    } catch (IOException e) {
      throw new RuntimeException("UserStatus 데이터 삭제 실패", e);
    } finally {
      lock.unlock();
    }
  }

  private void writeToFile(UserStatus userStatus) {
    Path path = dirPath.resolve(userStatus.getId() + ".ser");
    ReentrantLock lock = fileLockProvider.getLock(path);
    lock.lock();
    try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(path))) {
      oos.writeObject(userStatus);
    } catch (IOException e) {
      throw new RuntimeException("UserStatus 데이터 저장 실패", e);
    } finally {
      lock.unlock();
    }
  }
}
