package com.sprint.mission.discodeit.repository.file;


import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.stereotype.Repository;
import java.io.*;
import java.util.*;

@Repository
public class FileReadStatusRepository implements ReadStatusRepository {

    private static final String FILE_PATH = "readStatus.dat";

    private Map<UUID, ReadStatus> loadReadStatusFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new HashMap<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, ReadStatus>) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void saveReadStatusFile(Map<UUID, ReadStatus> map) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(map);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        Map<UUID, ReadStatus> map = loadReadStatusFile();
        map.put(readStatus.getId(), readStatus);
        saveReadStatusFile(map);
        return readStatus;
    }

    @Override
    public ReadStatus findById(UUID id) {
        return loadReadStatusFile().get(id);
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return loadReadStatusFile().values().stream()
                .filter(rs -> rs.getUserId().equals(userId))
                .toList();
    }

    @Override
    public ReadStatus findByUserIdAndChannelId(UUID userId, UUID channelId) {
        return loadReadStatusFile().values().stream()
                .filter(rs -> rs.getUserId().equals(userId) && rs.getChannelId().equals(channelId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void delete(UUID id) {
        Map<UUID, ReadStatus> map = loadReadStatusFile();
        map.remove(id);
        saveReadStatusFile(map);
    }

    @Override
    public void deleteByChannelId(UUID channelId) {
        Map<UUID, ReadStatus> map = loadReadStatusFile();
        map.values().removeIf(rs -> rs.getChannelId().equals(channelId));
        saveReadStatusFile(map);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        Map<UUID, ReadStatus> map = loadReadStatusFile();
        map.values().removeIf(rs -> rs.getUserId().equals(userId));
        saveReadStatusFile(map);
    }
}

