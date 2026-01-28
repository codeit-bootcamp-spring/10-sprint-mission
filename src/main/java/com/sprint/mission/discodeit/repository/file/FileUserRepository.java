package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.DuplicationEmailException;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.util.*;

public class FileUserRepository implements UserRepository {

    private static final String FILE_PATH = "users.dat";

    // 파일에서 Map 로드
    private Map<UUID, User> load() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new LinkedHashMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, User>) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 파일에 Map 저장
    private void save(Map<UUID, User> users) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(users);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 초기화
    public void resetUserFile() {
        save(new LinkedHashMap<>());
    }

    @Override
    public User createUser(User user) {
        Map<UUID, User> users = load();
        if (users.values().stream().anyMatch(u -> u.getUserEmail().equals(user.getUserEmail()))) {
            throw new DuplicationEmailException();
        }
        users.put(user.getId(), user);
        save(users);
        return user;
    }

    @Override
    public User findUser(UUID userId) {
        User user = load().get(userId);
        if (user == null) throw new UserNotFoundException();
        return user;
    }

    @Override
    public List<User> findAllUser() {
        return new ArrayList<>(load().values());
    }

    @Override
    public User updateUser(UUID userId, String userName, String userEmail) {
        Map<UUID, User> users = load();
        User user = users.get(userId);
        if (user == null) throw new UserNotFoundException();

        user.update(userName, userEmail);
        save(users);
        return user;
    }

    @Override
    public User deleteUser(UUID userId) {
        Map<UUID, User> users = load();
        User removed = users.remove(userId);
        if (removed == null) throw new UserNotFoundException();
        save(users);
        return removed;
    }
}
