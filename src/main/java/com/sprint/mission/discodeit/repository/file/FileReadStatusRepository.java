package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;

@Repository
public class FileReadStatusRepository implements ReadStatusRepository {

    private static final String FILE_PATH = "read_statuses.dat";
    private final Map<UUID, ReadStatus> data;

    public FileReadStatusRepository() {
        this.data = loadFromFile();
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        data.put(readStatus.getId(), readStatus);
        saveToFile();
        return readStatus;
    }

    @Override
    public Optional<ReadStatus> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {
        if (userId == null || channelId == null) return Optional.empty();
        for (ReadStatus rs : data.values()) {
            if (userId.equals(rs.getUserId()) && channelId.equals(rs.getChannelId())) {
                return Optional.of(rs);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<ReadStatus> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        if (userId == null) return List.of();
        List<ReadStatus> result = new ArrayList<>();
        for (ReadStatus rs : data.values()) {
            if (userId.equals(rs.getUserId())) result.add(rs);
        }
        return result;
    }

    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        if (channelId == null) return List.of();
        List<ReadStatus> result = new ArrayList<>();
        for (ReadStatus rs : data.values()) {
            if (channelId.equals(rs.getChannelId())) result.add(rs);
        }
        return result;
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
        saveToFile();
    }

    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new IllegalStateException("read_statuses.dat 저장 실패", e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, ReadStatus> loadFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new HashMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            return (obj instanceof Map) ? (Map<UUID, ReadStatus>) obj : new HashMap<>();
        } catch (IOException | ClassNotFoundException e) {
            return new HashMap<>();
        }
    }
}
