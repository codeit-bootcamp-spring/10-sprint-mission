package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileUserRepository implements UserRepository {
    private final String FILE_PATH = "User.ser";

    private List<User> loadData() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<User>) ois.readObject();
        } catch (Exception e) { return new ArrayList<>(); }
    }

    private void saveData(List<User> data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(data);
        } catch (IOException e) { e.printStackTrace(); }
    }

    @Override
    public User save(User user) {
        List<User> data = loadData();
        Optional<User> existingUsers = data.stream()
                .filter(u -> u.getId().equals(user.getId()))
                .findFirst();

        if (existingUsers.isPresent()) {
            User existingUser = existingUsers.get();
            existingUser.update(user.getName(), user.getEmail(), user.getPassword());
        } else {
            data.add(user);
        }
        saveData(data);
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return loadData().stream()
                .filter(u -> u.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return loadData();
    }

    @Override
    public void delete(UUID id) {
        List<User> data = loadData();
        data.removeIf(u -> u.getId().equals(id));
        saveData(data);

    }
}
