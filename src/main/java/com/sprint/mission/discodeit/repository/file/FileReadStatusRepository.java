package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;

import java.io.*;
import java.util.*;

public class FileReadStatusRepository implements ReadStatusRepository {
    private final static String FILE_PATH = "read_status.ser";
    private Map<UUID, ReadStatus> data;

    public FileReadStatusRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        data.put(readStatus.getId(), readStatus);
        saveToFile();
        return readStatus;
    }

    @Override
    public Optional<ReadStatus> findById(UUID readStatusId) {
        return Optional.ofNullable(data.get(readStatusId));
    }

    @Override
    public List<ReadStatus> findByUserId(UUID userId) {
        return data.values().stream()
                .filter(readStatus -> readStatus.getUserId().equals(userId))
                .toList();
    }

    @Override
    public List<ReadStatus> findAll() {
        return data.values().stream().toList();
    }

    @Override
    public boolean existsByUserIdAndChannelId(UUID userId, UUID channelId) {
        boolean isExists = data.values().stream()
                .filter(readStatus -> readStatus.getUserId().equals(userId))
                .anyMatch(readStatus -> readStatus.getChannelId().equals(channelId));

        return isExists;
    }

    @Override
    public void deleteById(UUID readStatusId) {
        data.remove(readStatusId);
        saveToFile();
    }

    @Override
    public void deleteByChannelId(UUID channelId) {
        data.values().removeIf(
                readStatus -> readStatus.getChannelId().equals(channelId)
        );
        saveToFile();
    }

    @Override
    public void deleteByUserIdAndChannelId(UUID userId, UUID channelId) {
        data.values().removeIf(rs ->
                rs.getUserId().equals(userId) && rs.getChannelId().equals(channelId)
        );
        saveToFile();
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, Channel> load() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return new HashMap<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, Channel>) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException("읽음 상태 데이터 로드 실패", e);
        }
    }

    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
