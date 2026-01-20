package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileUserRepository implements UserRepository {
    private final Path basePath = Path.of("data/user");
    private final Path storeFile = basePath.resolve("user.ser");

    private List<User> userData;
    private MessageService messageService;
    private ChannelService channelService;

    // constructor
    public FileUserRepository() {
        init();
        loadData();
    }

    // 디렉토리 체크
    private void init() {
        try {
            if (!Files.exists(basePath)) {
                Files.createDirectories(basePath);
            }
        } catch (IOException e) {
            System.out.println("Directory creation failed." + e.getMessage());
        }
    }

    // 저장 (직렬화)
    void saveData() {
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(storeFile.toFile()))) {

            oos.writeObject(userData);

        } catch (IOException e) {

            throw new RuntimeException("Data save failed." + e.getMessage());

        }
    }

    // 로드 (역직렬화)
    void loadData() {
        // 파일이 없으면: 첫 실행이므로 빈 리스트 유지
        if (!Files.exists(storeFile)) {
            userData = new ArrayList<>();
            return;
        }

        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(storeFile.toFile()))){
            userData = (List<User>) ois.readObject();
        } catch (Exception e){
            throw new RuntimeException("Data load failed." + e.getMessage());
        }
    }


    @Override
    public User find(UUID userID) {
        loadData();
        return userData.stream()
                .filter(user -> user.getId().equals(userID))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userID));
    }

    @Override
    public List<User> findAll() {
        loadData();
        return userData;
    }

    @Override
    public void addUser(User user) {
        loadData();
        userData.add(user);
        saveData();
    }

    @Override
    public void removeUser(User user) {
        loadData();
        userData.removeIf(u -> u.getId().equals(user.getId()));
        saveData();
    }

    @Override
    public User save(User user){
        loadData();
        userData.removeIf(u -> u.getId().equals(user.getId()));
        userData.add(user);
        saveData();
        return user;
    }
}
