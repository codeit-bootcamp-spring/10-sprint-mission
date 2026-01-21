package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.io.*;
import java.util.*;

public class FileUserRepository implements UserRepository {
    private final String FILE_PATH = "users.dat";

    @SuppressWarnings("unchecked")
    private List<User> load() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    private void saveAll(List<User> users) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(User user) {
        List<User> users = load();
        users.add(user);
        saveAll(users);
    }

    @Override
    public User findById(UUID id) {
        return load().stream().filter(u -> u.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public List<User> findAll() {
        return load();
    }

    @Override
    public void update(User user) {
        List<User> users = load();
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(user.getId())) {
                users.set(i, user);
                saveAll(users);
                return;
            }
        }
    }

    @Override
    public void delete(UUID id) {
        List<User> users = load();
        users.removeIf(u -> u.getId().equals(id));
        saveAll(users);
    }
}