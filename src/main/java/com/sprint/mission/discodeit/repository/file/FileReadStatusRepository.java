package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
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
import java.util.stream.Stream;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileReadStatusRepository implements ReadStatusRepository {

    private final Path dirPath;

    public FileReadStatusRepository(@Value("${discodeit.repository.file-directory}") String dir) {
        this.dirPath = Paths.get(dir + "/readstatuses");
        init();
    }

    private void init() {
        if (!Files.exists(dirPath)) {
            try {
                Files.createDirectories(dirPath);
            } catch (IOException e) {
                throw new RuntimeException("ReadStatus 데이터 폴더 생성 실패", e);
            }
        }
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        writeToFile(readStatus);
        return readStatus;
    }

    @Override
    public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {
        return findAll().stream()
                .filter(readStatus -> readStatus.getUserId().equals(userId))
                .filter(readStatus -> readStatus.getChannelId().equals(channelId))
                .findFirst();
    }

    @Override
    public Optional<ReadStatus> findById(UUID readStatusId) {
        Path path = dirPath.resolve(readStatusId.toString() + ".ser");
        if (!Files.exists(path)) {
            return Optional.empty();
        }
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(path))) {
            return Optional.ofNullable((ReadStatus) ois.readObject());
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException("ReadStatus 데이터 조회 실패", e);
        }

    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return findAll().stream()
                .filter(readStatus -> readStatus.getUserId().equals(userId))
                .toList();
    }

    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        return findAll().stream()
                .filter(readStatus -> readStatus.getChannelId().equals(channelId))
                .toList();
    }

    @Override
    public List<ReadStatus> findAll() {
        if (Files.exists(dirPath)) {
            return List.of();
        }
        try (Stream<Path> stream = Files.list(dirPath)) {
            return stream
                    .map(path -> {
                        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(path))) {
                            return (ReadStatus) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException("ReadStatus 데이터 조회 실패", e);
                        }
                    })
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("ReadStatus 데이터 목록 조회 실패", e);
        }
    }

    @Override
    public void delete(ReadStatus readStatus) {
        Path path = dirPath.resolve(readStatus.getId().toString() + ".ser");
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException("ReadStatus 데이터 삭제 실패", e);
        }
    }

    private void writeToFile(ReadStatus readStatus) {
        Path path = dirPath.resolve(readStatus.getId().toString() + ".ser");
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(path))) {
            oos.writeObject(readStatus);
        } catch (IOException e) {
            throw new RuntimeException("ReadStatus 데이터 저장 실패", e);
        }
    }
}
