package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FileUserService implements UserService {
    private final File file;

    public FileUserService(String path) {
        file = new File(path);
    }

    private void save(Map<UUID, User> data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, User> load(){
        if (!file.exists()) {
            return new HashMap<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public User create(User user) {
        Map<UUID, User> data = load();
        data.put(user.getId(), user);
        save(data);
        return user;
    }

    @Override
    public User read(UUID id) {
        Map<UUID, User> data = load();
        return data.get(id);
    }

    @Override
    public List<User> readAll() {
        return List.copyOf(load().values());
    }

    @Override
    public User update(User user) {
        Map<UUID, User> data = load();
        User found = data.get(user.getId());
        if (found == null) {
            throw new IllegalArgumentException("존재하지 않는 유저입니다.");
        }

        found.updateName(user.getName());
        found.updateStatus(user.getStatus());
        found.updateUpdatedAt(System.currentTimeMillis());

        data.put(user.getId(), found);
        save(data);
        return found;
    }

    @Override
    public void delete(UUID id) {
        Map<UUID, User> data = load();
        data.remove(id);
        save(data);
    }

    @Override
    public void clear() {
        save(new HashMap<>());
    }
}
