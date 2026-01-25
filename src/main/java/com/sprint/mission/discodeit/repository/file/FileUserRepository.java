package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileUserRepository implements UserRepository {
    private final String FILE_PATH = "user.dat";

    private List<User> loadData() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    private void saveData(List<User> users) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("user.dat"))) {
            oos.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void save(User user) {
        List<User> data = loadData();
        Optional<User> existingUsers = data.stream()
                .filter(u -> u.getId().equals(user.getId()))
                .findFirst();

        if (existingUsers.isPresent()) {
            User existingUser = existingUsers.get();
            existingUser.update(user.getId(), user.getEmail(), user.getEmail());
        } else {
            data.add(user);
        }
        saveData(data);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        return List.of();
    }

    @Override
    public void delete(UUID id) {

    }

    @Override
    public void deleteAll() {

    }
}
