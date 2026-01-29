package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
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
    public UserDto.UserResponse create(UserDto.UserCreateRequest request) {
        // 비즈니스 로직
        Objects.requireNonNull(request.name(), "유저 이름은 필수 항목입니다.");
        Objects.requireNonNull(request.email(), "이메일은 필수 항목입니다.");

        if (data.values().stream().anyMatch(user -> user.getName().equals(request.name()))) {
            throw new IllegalArgumentException("이미 존재하는 이름입니다.");
        }
        if (data.values().stream().anyMatch(user -> user.getEmail().equals(request.email()))) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        User user = new User(request.name(), request.email());

        // 저장 로직
        data.put(user.getId(), user);
        saveToFile();
        return UserDto.UserResponse.from(user, new UserStatus(user));
    }

    @Override
    public UserDto.UserResponse findById(UUID userId) {
        User user = Optional.ofNullable(data.get(userId))
                .orElseThrow(() -> new NoSuchElementException("해당 유저가 존재하지 않습니다."));
        return UserDto.UserResponse.from(user, new UserStatus(user));
    }

    @Override
    public List<UserDto.UserResponse> findAll() {
        return data.values().stream()
                .map(user -> UserDto.UserResponse.from(user, new UserStatus(user)))
                .toList();
    }

    @Override
    public UserDto.UserResponse update(UUID userId, UserDto.UserUpdateRequest request) {
        User user = Optional.ofNullable(data.get(userId))
                .orElseThrow(() -> new NoSuchElementException("해당 유저가 존재하지 않습니다."));

        Optional.ofNullable(request.name()).ifPresent(user::updateName);
        Optional.ofNullable(request.email()).ifPresent(user::updateEmail);

        saveToFile();
        return UserDto.UserResponse.from(user, new UserStatus(user));
    }

    @Override
    public void delete(UUID userId) {
        if (!data.containsKey(userId)) {
            throw new NoSuchElementException("해당 유저가 존재하지 않습니다.");
        }

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