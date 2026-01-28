package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import java.io.*;
import java.util.*;

public class FileUserService implements UserService {
    private final String FILE_PATH = "users.dat";

    @SuppressWarnings("unchecked")
    private List<User> loadUsers() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    private void saveUsers(List<User> users) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public User create(String username) {
        List<User> users = loadUsers();
        User newUser = new User(username);
        users.add(newUser);
        saveUsers(users);
        return newUser;
    }

    @Override
    public User findById(UUID userId) {
        return loadUsers().stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<User> findAll() {
        return loadUsers();
    }

    @Override
    public User update(UUID userId, String username) {
        List<User> users = loadUsers();
        for (User user : users) {
            if (user.getId().equals(userId)) {
                user.updateUsername(username);
                saveUsers(users);
                return user;
            }
        }
        return null;
    }

    @Override
    public void delete(UUID userId) {
        List<User> users = loadUsers();
        users.removeIf(u -> u.getId().equals(userId));
        saveUsers(users);
    }

    @Override
    public List<User> findUsersByChannelId(UUID channelId) {
        List<User> result = new ArrayList<>();
        for (User user : loadUsers()) {
            boolean isParticipant = user.getChannels().stream()
                    .anyMatch(c -> c.getId().equals(channelId));
            if (isParticipant) {
                result.add(user);
            }
        }
        return result;
    }

    @Override
    public List<Message> findMessagesByUserId(UUID userId) {
        User user = findById(userId);
        return (user != null) ? user.getMessages() : new ArrayList<>();
    }
}