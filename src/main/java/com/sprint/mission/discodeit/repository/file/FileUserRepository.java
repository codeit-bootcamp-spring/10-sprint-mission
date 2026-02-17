package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Repository
@ConditionalOnProperty(
        prefix = "discodeit.repository",
        name = "type",
        havingValue = "file"
)
public class FileUserRepository implements UserRepository {

    private final Path filePath;

    public FileUserRepository(
            @Value("${discodeit.repository.file-directory:.discodeit}") String fileDirectory
    ) {
        try {
            Files.createDirectories(Paths.get(fileDirectory));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.filePath = Paths.get(fileDirectory, "users.dat");
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, User> loadUserFile() {
        File file = filePath.toFile();
        if (!file.exists()) return new LinkedHashMap<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof Map<?, ?>) return (Map<UUID, User>) obj;
            return new LinkedHashMap<>();
        } catch (EOFException e) {
            return new LinkedHashMap<>();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void saveUserFile(Map<UUID, User> users) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
            oos.writeObject(users);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void resetFile() {
        saveUserFile(new LinkedHashMap<>());
    }

    @Override
    public synchronized User save(User user) {
        Map<UUID, User> users = loadUserFile();
        users.put(user.getId(), user);
        saveUserFile(users);
        return user;
    }

    @Override
    public synchronized User findById(UUID id) {
        return loadUserFile().get(id);
    }

    @Override
    public synchronized List<User> findAll() {
        return new ArrayList<>(loadUserFile().values());
    }

    @Override
    public synchronized void delete(UUID id) {
        Map<UUID, User> users = loadUserFile();
        users.remove(id); // 없으면 그냥 무시
        saveUserFile(users);
    }

    @Override
    public synchronized boolean existsByEmail(String email) {
        return loadUserFile().values().stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }

    @Override
    public synchronized boolean existsByName(String name) {
        return loadUserFile().values().stream()
                .anyMatch(user -> user.getName().equals(name));
    }
}
