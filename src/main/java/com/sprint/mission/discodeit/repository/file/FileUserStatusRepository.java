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
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Repository
@ConditionalOnProperty(
        name = "discodeit.repository.type",
        havingValue = "file"
)
public class FileUserStatusRepository implements UserStatusRepository {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileUserStatusRepository(@Value("${discodeit.repository.file-directory}") String fileDirectory) {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"),
                fileDirectory, UserStatus.class.getSimpleName());
        if (Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException("UserStatus 디렉토리 생성 실패", e);
            }
        }
    }

    private Path resolve(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public UserStatus save(UserStatus userStatus) {
        Path path = resolve(userStatus.getUserId());
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(path.toFile()))) {
            oos.writeObject(userStatus);
        } catch (IOException e) {
            throw new RuntimeException("UserStatus 저장 실패", e);
        }
        return userStatus;
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        return readFromFile(resolve(id));
    }

    @Override
    public List<UserStatus> findAll() {
        try(Stream<Path> stream = Files.list(DIRECTORY)) {
            return stream
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(this::readFromFile)
                    .flatMap(Optional::stream)
                    .toList();
        } catch (IOException e) {
            return List.of();
        }
    }

    @Override
    public void deleteById(UUID id) {
        try {
            Files.deleteIfExists(resolve(id));
        } catch (IOException e) {
            throw new RuntimeException("UserStatus 삭제 실패", e);
        }
    }

    private Optional<UserStatus> readFromFile(Path path) {
        if (Files.notExists(path)) return Optional.empty();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
            return Optional.ofNullable((UserStatus) ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            return Optional.empty();
        }
    }
}

