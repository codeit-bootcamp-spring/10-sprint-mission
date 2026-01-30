package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileUserService implements UserService {

    private final List<User> data = new ArrayList<>();
    private final Path filePath;

    public FileUserService() {
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
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void save() {
        try {
            Files.createDirectories(filePath.getParent());
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
                oos.writeObject(data);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // UserService: User create(String username, String email, String password)
    @Override
    public User create(String username, String email, String password) {
        User user = new User(username, email, password); // 너가 말한대로 이 생성자 존재
        data.add(user);
        save();
        return user;
    }

    // UserService: User find(UUID userId)
    @Override
    public User find(UUID userId) {
        return data.stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data);
    }

    // UserService: User update(UUID userId, String newUsername, String newEmail, String newPassword)
    @Override
    public User update(UUID userId, String newUsername, String newEmail, String newPassword) {
        User user = find(userId);

        Optional.ofNullable(newUsername).ifPresent(user::setUsername);
        Optional.ofNullable(newEmail).ifPresent(user::setEmail);
        Optional.ofNullable(newPassword).ifPresent(user::setPassword);

        user.touch();   // User가 touch()를 가지거나(BaseEntity 상속 등) public이어야 함
        save();
        return user;
    }

    @Override
    public void delete(UUID userId) {
        User user = find(userId);
        data.remove(user);
        save();
    }
}
