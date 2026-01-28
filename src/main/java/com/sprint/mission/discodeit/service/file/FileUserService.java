package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.*;

public class FileUserService implements UserService {

    private final String FILE_PATH = "users.dat";
    private final Map<UUID, User> data; // 파일 저장 전 데이터 보관

    public FileUserService() {
        // 생성 시 파일에서 데이터 로드
        this.data = loadFromFile();
    }

    @Override
    public User create(String name, String email, String profileImageUrl) {
        // 비즈니스 로직
        Objects.requireNonNull(name, "유저 이름은 필수 항목입니다.");
        Objects.requireNonNull(email, "이메일은 필수 항목입니다.");
        Objects.requireNonNull(profileImageUrl, "프로필 이미지 URL은 필수 항목입니다.");

        User user = new User(name, email, profileImageUrl);

        // 저장 로직
        data.put(user.getId(), user);
        saveToFile();
        return user;
    }

    @Override
    public User findById(UUID userId) {
        Objects.requireNonNull(userId, "유저 Id가 유효하지 않습니다.");
        return Objects.requireNonNull(data.get(userId), "Id에 해당하는 유저가 존재하지 않습니다.");
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public User update(UUID userId, String name, String email, String profileImageUrl) {
        User user = findById(userId);

        Optional.ofNullable(name).ifPresent(user::updateName);
        Optional.ofNullable(email).ifPresent(user::updateEmail);
        Optional.ofNullable(profileImageUrl).ifPresent(user::updateProfileImageUrl);

        saveToFile();
        return user;
    }

    @Override
    public void delete(UUID userId) {
        findById(userId);

        data.remove(userId);
        saveToFile();
    }

    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, User> loadFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new HashMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new HashMap<>();
        }
    }
}