package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.util.*;

public class FileUserRepository implements UserRepository {
    private final File file = new File("users.dat");
    private Map<UUID, User> userMap;

    public FileUserRepository() {
        this.userMap = new HashMap<>();
    }

    // 역직렬화
    @SuppressWarnings("unchecked")
    private void load() {
        if (!file.exists()) {
            this.userMap = new HashMap<>();
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            this.userMap = (Map<UUID, User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("데이터 로드 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 직렬화
    private void saveFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(this.userMap);
        } catch (IOException e) {
            System.out.println("파일 저장 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public User save(User user) {
        load();
        userMap.put(user.getId(), user);
        saveFile();
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        load();
        return Optional.ofNullable(userMap.get(id));
    }

    @Override
    public List<User> findAll() {
        load();
        return new ArrayList<>(userMap.values());
    }

    @Override
    public List<User> findByChannelId(UUID channelId) {
        load();
        return userMap.values().stream()
                      .filter(user -> user.getMyChannels().stream()
                                          .anyMatch(channel -> channel.getId().equals(channelId)))
                      .toList();
    }

    @Override
    public void delete(UUID id) {
        load();
        userMap.remove(id);
        saveFile();
    }
}