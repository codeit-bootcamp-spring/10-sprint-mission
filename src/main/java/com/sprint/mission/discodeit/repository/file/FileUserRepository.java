package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;

@Repository
public class FileUserRepository implements UserRepository {

    private static final String FILE_PATH = "users.dat";
    private final Map<UUID, User> data;

    public FileUserRepository() {
        this.data = loadFromFile();
    }

    @Override
    public User save(User user) {
        data.put(user.getId(), user);
        saveToFile();
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
        saveToFile();
    }


    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new IllegalStateException("users.dat 저장에 실패했습니다.", e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, User> loadFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new HashMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            return (obj instanceof Map) ? (Map<UUID, User>) obj : new HashMap<>();
        } catch (IOException | ClassNotFoundException e) {
            return new HashMap<>();
        }
    }
}