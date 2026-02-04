package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.IntStream;

@Repository
@ConditionalOnProperty(
        prefix = "discodeit.repository",
        name = "type",
        havingValue = "file"
)
public class FileReadStatusRepository implements ReadStatusRepository {
    private static final String FILE_NAME = "readStatus.ser";
    private final Path filePath;
    private final List<ReadStatus> data;

    public FileReadStatusRepository(
            @Value("${discodeit.repository.file-directory:.discodeit}") String dir
    ) {
        this.filePath = Paths.get(dir, FILE_NAME);
        this.data = loadReadStatus();
    }

    private void saveReadStatus() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
            oos.writeObject(this.data);
            System.out.println("직렬화 완료: readStatus.ser에 저장되었습니다.");
        } catch (IOException e) {
            throw new RuntimeException("readStatus 저장 중 오류 발생", e);
        }
    }

    @SuppressWarnings("unchecked")
    private List<ReadStatus> loadReadStatus() {
        if (!filePath.toFile().exists() || filePath.toFile().length() == 0) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath.toFile()))) {
            Object data = ois.readObject();
            System.out.println("역직렬화 완료: " + data);
            return (List<ReadStatus>) data;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("readStatus 로드 중 오류 발생", e);
        }
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        OptionalInt indexOpt = IntStream.range(0, this.data.size())
                .filter(i -> data.get(i).getId().equals(readStatus.getId()))
                .findFirst();
        if (indexOpt.isPresent()) {
            data.set(indexOpt.getAsInt(), readStatus);
        } else {
            data.add(readStatus);
        }
        saveReadStatus();
        return readStatus;
    }

    @Override
    public List<ReadStatus> saveAll(List<ReadStatus> readStatus) {
        for (ReadStatus status : readStatus) {
            save(status);
        }
        return readStatus;
    }

    @Override
    public Optional<ReadStatus> findById(UUID id) {
        return data.stream()
                .filter(rs -> rs.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<ReadStatus> findAll() {
        return new ArrayList<>(data);
    }

    @Override
    public void deleteById(UUID id) {
        data.removeIf(rs -> rs.getId().equals(id));
        saveReadStatus();
    }

    @Override
    public void deleteAllByChannelId(UUID channelId) {
        data.removeIf(rs -> rs.getChannelId().equals(channelId));
        saveReadStatus();
    }
}
