package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileMessageRepository implements MessageRepository {

  private final Path dirPath;
  private final FileLockProvider fileLockProvider;

  public FileMessageRepository(
      @Value("${discodeit.repository.file-directory}") String dir,
      FileLockProvider fileLockProvider
  ) {
    this.dirPath = Paths.get(dir, "messages");
    this.fileLockProvider = fileLockProvider;
    init();
  }

  private void init() {
    if (!Files.exists(dirPath)) {
      try {
        Files.createDirectories(dirPath);
      } catch (IOException e) {
        throw new RuntimeException("Message 데이터 폴더 생성 실패", e);
      }
    }
  }

  @Override
  public Message save(Message message) {
    writeToFile(message);
    return message;
  }

  @Override
  public Optional<Message> findById(UUID messageId) {
    Path path = dirPath.resolve(messageId + ".ser");
    ReentrantLock lock = fileLockProvider.getLock(path);
    lock.lock();
    try {
      if (!Files.exists(path)) {
        return Optional.empty();
      }
      try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(path))) {
        return Optional.ofNullable((Message) ois.readObject());
      } catch (IOException | ClassNotFoundException e) {
        throw new RuntimeException("Message 데이터 조회 실패", e);
      }
    } finally {
      lock.unlock();
    }
  }

  @Override
  public List<Message> findAll() {
    if (!Files.exists(dirPath)) {
      return List.of();
    }
    try (Stream<Path> stream = Files.list(dirPath)) {
      return stream
          .map(path -> {
            ReentrantLock lock = fileLockProvider.getLock(path);
            lock.lock();
            try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(path))) {
              return (Message) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
              throw new RuntimeException("Message 데이터 조회 실패", e);
            } finally {
              lock.unlock();
            }
          })
          .toList();
    } catch (IOException e) {
      throw new RuntimeException("Message 데이터 목록 조회 실패", e);
    }
  }

  @Override
  public void delete(Message message) {
    Path path = dirPath.resolve(message.getId() + ".ser");
    ReentrantLock lock = fileLockProvider.getLock(path);
    lock.lock();
    try {
      Files.deleteIfExists(path);
    } catch (IOException e) {
      throw new RuntimeException("Message 데이터 삭제 실패", e);
    } finally {
      lock.unlock();
    }
  }

  private void writeToFile(Message message) {
    Path path = dirPath.resolve(message.getId() + ".ser");
    ReentrantLock lock = fileLockProvider.getLock(path);
    lock.lock();
    try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(path))) {
      oos.writeObject(message);
    } catch (IOException e) {
      throw new RuntimeException("Message 데이터 저장 실패", e);
    } finally {
      lock.unlock();
    }
  }
}
