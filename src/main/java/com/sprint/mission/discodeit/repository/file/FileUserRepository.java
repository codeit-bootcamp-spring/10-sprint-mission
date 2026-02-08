package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class FileUserRepository implements UserRepository {

    private final List<User> data = new ArrayList<>();
    private final Path filePath;

    public FileUserRepository() {
        this.filePath = Path.of("data", "users.ser");
        load();
    }

    private void load() {
        if (Files.notExists(filePath)) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath.toFile()))) {
            @SuppressWarnings("unchecked")
            List<User> loaded = (List<User>) ois.readObject();
            data.clear();
            data.addAll(loaded);
        } catch (InvalidClassException e) {
            System.out.println("이전 버전 파일을 무시하고 새로 시작합니다: " + e.getMessage());
            data.clear();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void save() {
        try {
            Files.createDirectories(filePath.getParent());
            try (ObjectOutputStream oos =
                         new ObjectOutputStream
                        (new FileOutputStream(filePath.toFile()))) {
                oos.writeObject(data);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User save(User user) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("User or User ID cannot be null");
        }
        // 기존 사용자 제거 후 추가 (업데이트 처리)
        data.removeIf(u -> u.getId().equals(user.getId()));
        data.add(user);
        save();
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return data.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return data.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return data.stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data);
    }

    @Override
    public boolean existsById(UUID id) {
        return data.stream()
                .anyMatch(u -> u.getId().equals(id));
    }

    @Override
    public boolean existsByUsername(String username) {
        return data.stream()
                .anyMatch(u -> u.getUsername().equals(username));
    }

    @Override
    public boolean existsByEmail(String email) {
        return data.stream()
                .anyMatch(u -> u.getEmail().equals(email));
    }

    @Override
    public void deleteById(UUID id) {
        data.removeIf(u -> u.getId().equals(id));
        save();
    }
}
