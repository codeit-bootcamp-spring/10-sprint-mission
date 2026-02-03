package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.util.SerializedFileUtils;
import org.springframework.stereotype.Repository;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class FileReadStatusRepository implements ReadStatusRepository {
    private static final String FILE_PREFIX = "readStatus";
    private static final String ENTITY_NAME = "읽음 상태";

    private final Map<UUID, ReadStatus> data; // 빠른 조회를 위한 컬렉션
    private final Path readStatusDir;

    public FileReadStatusRepository(Path baseDir) {
        this.data = new HashMap<>();
        this.readStatusDir = baseDir.resolve(FILE_PREFIX);
        try {
            // 파일이 저장될 디렉토리가 존재하지 않을 경우 폴더 생성
            Files.createDirectories(readStatusDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 생성 시 디렉토리와 컬렉션 동기화
        loadAllFromDirectory();
    }

    private Path readStatusFilePath(UUID readStatusId) {
        // 읽음 상태를 구분하기 위한 파일 경로 생성
        return readStatusDir.resolve(FILE_PREFIX + "-" + readStatusId + ".ser");
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        // 경로 생성 (readStatus-id.ser)
        Path filePath = readStatusFilePath(readStatus.getId());

        try (FileOutputStream fos = new FileOutputStream(filePath.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            // 파일 저장
            oos.writeObject(readStatus);
            data.put(readStatus.getId(), readStatus);
            return readStatus;
        } catch (IOException e) {
            throw new RuntimeException("읽음 상태 파일 저장을 실패했습니다.");
        }
    }

    @Override
    public Optional<ReadStatus> findById(UUID readStatusId) {
        return Optional.ofNullable(data.get(readStatusId));
    }

    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        return data.values().stream()
                .filter(readStatus -> readStatus.getChannelId().equals(channelId))
                .toList();
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return data.values().stream()
                .filter(readStatus -> readStatus.getUserId().equals(userId))
                .toList();
    }

    @Override
    public List<UUID> findParticipantUserIdsByChannelId(UUID channelId) {
        return data.values().stream()
                .filter(readStatus -> readStatus.getChannelId().equals(channelId))
                .map(ReadStatus::getUserId)
                .toList();
    }

    @Override
    public void delete(UUID readStatusId) {
        if (!data.containsKey(readStatusId)) {
            throw new RuntimeException("읽음 상태가 존재하지 않습니다.");
        }

        Path filePath = readStatusFilePath(readStatusId);
        SerializedFileUtils.deleteFileOrThrow(filePath, ENTITY_NAME);
        data.remove(readStatusId);
    }

    @Override
    public void deleteAllByChannelId(UUID channelId) {
        List<UUID> readStatusIdsToDelete = data.values().stream()
                .filter(readStatus -> readStatus.getChannelId().equals(channelId))
                .map(ReadStatus::getId)
                .toList();

        for (UUID readStatusId : readStatusIdsToDelete) {
            Path filePath = readStatusFilePath(readStatusId);
            SerializedFileUtils.deleteFileOrThrow(filePath, ENTITY_NAME);
            data.remove(readStatusId);
        }
    }


    public ReadStatus loadByIdFromFile(UUID readStatusId) {
        // 경로 생성 (readStatus-id.ser)
        Path filePath = readStatusFilePath(readStatusId);
        // 파일 역직렬화
        ReadStatus readStatus = (ReadStatus) SerializedFileUtils.deserialize(filePath, ENTITY_NAME);
        // 컬렉션과 동기화
        data.put(readStatus.getId(), readStatus);
        return readStatus;
    }

    private void loadAllFromDirectory() {
        data.clear();

        for (Object object : SerializedFileUtils.deserializeAll(readStatusDir, FILE_PREFIX, ENTITY_NAME)) {
            ReadStatus readStatus = (ReadStatus) object;
            data.put(readStatus.getId(), readStatus);
        }
    }
}
