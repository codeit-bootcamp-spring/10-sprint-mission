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
            List<User> loaded = (List<User>) ois.readObject(); // readObject로 List를 복원 [web:106]
            data.clear();
            data.addAll(loaded);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void save() {
        try {
            Files.createDirectories(filePath.getParent()); // 디렉토리 없으면 생성 [web:109]
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
                oos.writeObject(data); // writeObject로 List 통째 저장 [web:79]
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User createUser(String username, String email) {
        User user = new User(username, email);
        data.add(user);
        save();
        return user;
    }

    @Override
    public User findById(UUID id) {
        return data.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data);
    }

    @Override
    public User update(UUID id, String username, String email) {
        User user = findById(id);

        Optional.ofNullable(username).ifPresent(user::setUsername);
        Optional.ofNullable(email).ifPresent(user::setEmail);

        user.touch();
        save();
        return user;
    }

    @Override
    public void delete(UUID id) {
        User user = findById(id);
        data.remove(user);
        save();
    }
}
