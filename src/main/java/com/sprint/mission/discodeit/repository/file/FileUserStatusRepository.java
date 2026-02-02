package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileUserStatusRepository implements UserStatusRepository {
    private final File file;
    private final Map<UUID, UserStatus> cache;

    public FileUserStatusRepository(@Value("${discodeit.repository.file-directory:.discodeit}") String directory) {
        File dir = new File(directory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        this.file = new File(dir, "user_statuses.dat");
        this.cache = loadData();
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, UserStatus> loadData() {
        if (!file.exists()) return new HashMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, UserStatus>) ois.readObject();
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(cache);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(UserStatus userStatus) {
        cache.put(userStatus.getId(), userStatus);
        saveData();
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        return Optional.ofNullable(cache.get(id));
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return cache.values().stream()
                .filter(us -> us.getUserId().equals(userId))
                .findFirst();
    }

    @Override
    public List<UserStatus> findAll() {
        return new ArrayList<>(cache.values());
    }

    @Override
    public void delete(UUID id) {
        cache.remove(id);
        saveData();
    }
}
