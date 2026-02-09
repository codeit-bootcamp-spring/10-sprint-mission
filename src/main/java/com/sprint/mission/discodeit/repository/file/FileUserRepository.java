package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileUserRepository implements UserRepository {
    private final Map<UUID, User> data;
    private final FileObjectStore fileObjectStore;

    public FileUserRepository(FileObjectStore fileObjectStore) {
        this.data = fileObjectStore.getUsersData();
        this.fileObjectStore = fileObjectStore;
    }

    @Override
    public void save(User user) {
        data.put(user.getId(), user);
        fileObjectStore.saveData();
    }

    @Override
    public Optional<User> findById(UUID userId) {
        return Optional.ofNullable(data.get(userId));
    }

    @Override
    public Optional<User> findByUserNameAndPassword(String userName, String password) {
        return this.data.values().stream()
                .filter(u -> u.getUsername().equals(userName) && u.getPassword().equals(password))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void delete(UUID userId) {
        data.remove(userId);
        fileObjectStore.saveData();

    }

    @Override
    public boolean existUserName(String newUserName) {
        return this.data.values().stream()
                .anyMatch(user -> user.getUsername().equals(newUserName));
    }

    @Override
    public boolean existEmail(String newEmail) {
        return this.data.values().stream()
                .anyMatch(user -> user.getEmail().equals(newEmail));
    }

    @Override
    public boolean isEmailUsedByOther(UUID userId, String newEmail) {
        return this.data.values().stream()
                .anyMatch(user -> !user.getId().equals(userId) && user.getEmail().equals(newEmail));
    }

    @Override
    public boolean isUserNameUsedByOther(UUID userId, String newUserName) {
        return this.data.values().stream()
                .anyMatch(user -> !user.getId().equals(userId) && user.getUsername().equals(newUserName));
    }
}
