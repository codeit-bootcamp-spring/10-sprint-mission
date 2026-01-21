package org.example.repository.file;

import org.example.entity.User;
import org.example.repository.UserRepository;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileUserRepository implements UserRepository {

    private static final Path FILE_PATH = Paths.get("data", "users.ser");

    public FileUserRepository() {
        initializeFile();
    }

    // ============================================
    // 파일 I/O
    // ============================================

    private void initializeFile() {
        try {
            if (Files.notExists(FILE_PATH.getParent())) {
                Files.createDirectories(FILE_PATH.getParent());
            }
            if (Files.notExists(FILE_PATH)) {
                saveToFile(new HashMap<>());
            }
        } catch (IOException e) {
            throw new RuntimeException("파일 초기화 실패", e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, User> loadFromFile() {
        if (Files.notExists(FILE_PATH)) {
            return new HashMap<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(FILE_PATH))) {
            return (Map<UUID, User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("유저 데이터 로드 실패", e);
        }
    }

    private void saveToFile(Map<UUID, User> data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(FILE_PATH))) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException("유저 데이터 저장 실패", e);
        }
    }

    // ============================================
    // Repository 구현
    // ============================================

    @Override
    public User save(User user) {
        Map<UUID, User> data = loadFromFile();
        data.put(user.getId(), user);
        saveToFile(data);
        return user;
    }

    @Override
    public Optional<User> findById(UUID userId) {
        Map<UUID, User> data = loadFromFile();
        return Optional.ofNullable(data.get(userId));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(loadFromFile().values());
    }

    @Override
    public void deleteById(UUID userId) {
        Map<UUID, User> data = loadFromFile();
        data.remove(userId);
        saveToFile(data);
    }

    @Override
    public boolean existsById(UUID userId) {
        return loadFromFile().containsKey(userId);
    }
}
