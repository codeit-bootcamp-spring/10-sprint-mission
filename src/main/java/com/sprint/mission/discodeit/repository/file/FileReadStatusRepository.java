package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.BusinessException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
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

@Repository
@ConditionalOnProperty(
        name = "discodeit.repository.type",
        havingValue = "file"
)
public class FileReadStatusRepository implements ReadStatusRepository {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileReadStatusRepository(@Value("${discodeit.repository.file-directory}") String fileDirectory) {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"),
                fileDirectory, ReadStatus.class.getSimpleName());
        if (Files.notExists(DIRECTORY))
            try {
            Files.createDirectories(DIRECTORY);
        } catch (IOException e){
                throw new BusinessException(ErrorCode.DIRECTORY_CREATE_FAILED);
            }
    }

    private Path resolve(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        Path path = resolve(readStatus.getId());
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(path.toFile()))) {
            oos.writeObject(readStatus);
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.FILE_SAVE_ERROR);
        }
        return readStatus;
    }

    @Override
    public Optional<ReadStatus> findById(UUID id) {
        return readFromFile(resolve(id));
    }

    @Override
    public List<ReadStatus> findAll() {
        try (var stream = Files.list(DIRECTORY)) {
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
            throw new BusinessException(ErrorCode.FILE_DELETE_ERROR);
        }
    }

    // [헬퍼 메서드]: 역직렬화 로직 공통화
    private Optional<ReadStatus> readFromFile(Path path) {
        if (Files.notExists(path)) {
            return Optional.empty();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
            return Optional.ofNullable((ReadStatus) ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            return Optional.empty();
        }
    }
}
