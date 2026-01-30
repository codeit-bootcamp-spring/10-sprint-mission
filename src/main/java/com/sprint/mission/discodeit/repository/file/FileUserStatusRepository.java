package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;

@Repository
public class FileUserStatusRepository implements UserStatusRepository {

    private static final String FILE_PATH = "userStatus.dat";

    private Map<UUID, UserStatus> loadUserStatusFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new HashMap<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, UserStatus>) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void saveUserStatusFile(Map<UUID, UserStatus> map) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(map);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserStatus save(UserStatus status) {
        Map<UUID, UserStatus> map = loadUserStatusFile();
        map.put(status.getId(), status);
        saveUserStatusFile(map);
        return status;
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        return Optional.ofNullable(loadUserStatusFile().get(id));
    }


    @Override
    public List<UserStatus> findAll() {
        return new ArrayList<>(loadUserStatusFile().values());
    }

    @Override
    public UserStatus findByUserId(UUID userId) {
        return loadUserStatusFile().values().stream()
                .filter(status -> status.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("UserStatus not found"));
    }

    @Override
    public void delete(UUID id) {
        Map<UUID, UserStatus> map = loadUserStatusFile();
        map.remove(id);
        saveUserStatusFile(map);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        Map<UUID, UserStatus> map = loadUserStatusFile();
        map.values().removeIf(status -> status.getUserId().equals(userId));
        saveUserStatusFile(map);
    }
}
