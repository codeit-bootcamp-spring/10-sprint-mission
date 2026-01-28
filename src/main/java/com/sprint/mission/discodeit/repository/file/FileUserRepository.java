package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class FileUserRepository implements UserRepository {
    private static final Path BASE_PATH = Path.of("data/user");
    private static final Path STORE_FILE = BASE_PATH.resolve("user.ser");

    private List<User> userData;

    // constructor
    public FileUserRepository() {
        init();
        loadData();
    }

    // 디렉토리 체크
    private void init() {
        try {
            if (!Files.exists(BASE_PATH)) {
                Files.createDirectories(BASE_PATH);
            }
        } catch (IOException e) {
            System.out.println("Directory creation failed." + e.getMessage());
        }
    }

    // 저장 (직렬화)
    void saveData() {
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(STORE_FILE.toFile()))) {

            oos.writeObject(userData);

        } catch (IOException e) {

            throw new RuntimeException("Data save failed." + e.getMessage());

        }
    }

    // 로드 (역직렬화)
    void loadData() {
        // 파일이 없으면: 첫 실행이므로 빈 리스트 유지
        if (!Files.exists(STORE_FILE)) {
            userData = new ArrayList<>();
            return;
        }

        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(STORE_FILE.toFile()))){
            userData = (List<User>) ois.readObject();
        } catch (Exception e){
            throw new RuntimeException("Data load failed." + e.getMessage());
        }
    }


    @Override
    public Optional<User> find(UUID userID) {
        loadData();
        return userData.stream()
                .filter(user -> user.getId().equals(userID))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        loadData();
        return userData;
    }

    @Override
    public void deleteUser(User user) {
        loadData();
        userData.removeIf(u -> u.getId().equals(user.getId()));
        saveData();
    }

    @Override
    public User save(User user){
        loadData();
        for (int i = 0; i < userData.size(); i++){
            if(userData.get(i).getId().equals(user.getId())){
                userData.set(i, user);
                saveData();
                return user;
            }
        }
        userData.add(user);
        saveData();
        return user;
    }
}
