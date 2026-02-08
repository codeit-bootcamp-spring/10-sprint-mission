package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileReadStatusRepository implements ReadStatusRepository {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";
    String className = this.getClass().getSimpleName();

    public FileReadStatusRepository() {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map", className);
    }

    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        Path path = resolvePath(readStatus.getId());
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(path))) {
            oos.writeObject(readStatus);
        } catch (IOException e) {
            throw new RuntimeException(className + "파일 생성 실패", e);
        }
        return readStatus;
    }

    @Override
    public Optional<ReadStatus> findById(UUID id) {
        ReadStatus readStatus = null;
        Path path = resolvePath(id);
        if(Files.exists(path)) {
            try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(path))) {
                readStatus = (ReadStatus) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(className + "파일 읽기 실패", e);
            }
        }
        return Optional.ofNullable(readStatus);
    }

    @Override
    public List<ReadStatus> findAll() {
        try {
            return Files.list(DIRECTORY)
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            return (ReadStatus) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        return List.of();
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return List.of();
    }

    @Override
    public boolean existsById(UUID id) {
        return false;
    }

    @Override
    public void deleteById(UUID id) {

    }
}
