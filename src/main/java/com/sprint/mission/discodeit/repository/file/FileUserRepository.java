package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.util.*;

public class FileUserRepository implements UserRepository {
    private Map<UUID, User> userDB = new HashMap<>();
    private final File file = new File("users.dat");

    public FileUserRepository() {
        if (file.exists()) load(); else persist();
    }

    @SuppressWarnings("unchecked")
    private void load() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            this.userDB = (Map<UUID, User>) ois.readObject();
        } catch (Exception e) {
            System.err.println("User 데이터 로드 실패: " + e.getMessage());
            this.userDB = new HashMap<>();
        }
    }

    private void persist() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(this.userDB);
        } catch (IOException e) {
            System.err.println("User 데이터 저장 실패: " + e.getMessage());
        }
    }

    @Override
    public User save(User user) {
        userDB.put(user.getId(), user);
        persist();
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(userDB.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(userDB.values());
    }

    @Override
    public void delete(UUID id) {
        userDB.remove(id);
        persist();
    }

    @Override
    public boolean existsByUsername(String username) {
        return userDB.values().stream().anyMatch(user -> user.getUsername().equals(username));
    }
}