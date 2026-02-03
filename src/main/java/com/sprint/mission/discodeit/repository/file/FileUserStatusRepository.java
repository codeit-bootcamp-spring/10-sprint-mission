package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;

@Repository
public class FileUserStatusRepository implements UserStatusRepository {

    private static final String FILE_PATH = "user_statuses.dat";
    private final Map<UUID, UserStatus> data;

    public FileUserStatusRepository() {
        this.data = loadFromFile();
    }

    @Override
    public UserStatus save(UserStatus userStatus) {
        data.put(userStatus.getId(), userStatus);
        saveToFile();
        return userStatus;
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        if (userId == null) return Optional.empty();
        for (UserStatus us : data.values()) {
            if (userId.equals(us.getUserId())) return Optional.of(us);
        }
        return Optional.empty();
    }

    @Override
    public List<UserStatus> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void deleteById(UUID id) {
        data.remove(id);
        saveToFile();
    }

    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new IllegalStateException("user_statuses.dat 저장 실패", e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, UserStatus> loadFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new HashMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            return (obj instanceof Map) ? (Map<UUID, UserStatus>) obj : new HashMap<>();
        } catch (IOException | ClassNotFoundException e) {
            return new HashMap<>();
        }
    }
}
