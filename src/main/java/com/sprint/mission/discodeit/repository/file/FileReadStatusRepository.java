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

@Repository
@ConditionalOnProperty(prefix = "discodeit.repository", name = "type", havingValue = "file")
public class FileReadStatusRepository implements ReadStatusRepository {
    private final Path readStatusPath;

    public FileReadStatusRepository(
            @Value("${discodeit.repository.file-directory:data}") String rootPath
    ) {
        this.readStatusPath = Paths.get(rootPath, "read-statuses");
    }

    @Override
    public Optional<ReadStatus> findById(UUID id) {
        Path readStatusPath = getReadStatusPath(id);
        return Optional.ofNullable(read(readStatusPath));
    }

    @Override
    public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {
        return findAll().stream()
                .filter(rs -> rs.getUserId().equals(userId) && rs.getChannelId().equals(channelId))
                .findFirst();
    }

    @Override
    public List<ReadStatus> findAll() {
        if(Files.exists(readStatusPath)) {
            try {
                return Files.list(readStatusPath)
                        .map(this::read)
                        .toList();
            } catch (IOException e) {
                throw new RuntimeException("ReadStatus 파일 목록을 불러오는데 실패했습니다.");
            }
        } else
            return List.of();
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return findAll().stream()
                .filter(rs -> rs.getUserId().equals(userId))
                .toList();
    }

    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        return findAll().stream()
                .filter(rs -> rs.getChannelId().equals(channelId))
                .toList();
    }

    @Override
    public void save(ReadStatus readStatus) {
        Path readStatusPath = getReadStatusPath(readStatus.getId());
        write(readStatus, readStatusPath);

    }

    @Override
    public void deleteById(UUID id) {
        Path readStatusPath = getReadStatusPath(id);
        try {
            Files.delete(readStatusPath);
        } catch (IOException e) {
            throw new RuntimeException("ReadStatus 파일을 삭제하는데 실패했습니다.");
        }
    }

    private ReadStatus read(Path path) {
        if (!Files.exists(path))
            throw new IllegalStateException("해당 파일이 존재하지 않습니다.");

        try (FileInputStream fis = new FileInputStream(path.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (ReadStatus) ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("파일을 ReadStatus로 변환할 수 없습니다.");
        } catch (IOException e) {
            throw new RuntimeException("ReadStatus 파일이나 경로를 불러오는데 실패했습니다.");
        }
    }

    private void write(ReadStatus readStatus, Path path) {
        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(readStatus);
         } catch (IOException e) {
            throw new RuntimeException("ReadStatus를 파일로 저장하는데 실패했습니다.");
        }
    }


    private Path getReadStatusPath(UUID statusId) {
        try {
            Files.createDirectories(readStatusPath);
        } catch (IOException e) {
            throw new IllegalStateException("read-statuses 경로를 만드는데 실패했습니다.");
        }

        return readStatusPath.resolve(statusId.toString() + ".ser");
    }
}
