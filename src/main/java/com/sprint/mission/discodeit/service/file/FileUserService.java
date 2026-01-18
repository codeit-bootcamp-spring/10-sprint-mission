package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileUserService implements UserService {

    private static final String FILE_NAME = "users.ser";
    private final Path filePath;

    public FileUserService(String path) {
        this.filePath = Paths.get(path, FILE_NAME);
        init(filePath.getParent());
    }

    @Override
    public User create(String username, String email, String password) {
        List<User> data = load();
        existsByEmail(data, email);
        User user = new User(username, email, password);
        data.add(user);
        save(data);
        return user;
    }

    public User findUserById(UUID userId) {
        List<User> data = load();
        return data.stream()
                .filter(user -> user.getId().equals(userId))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }

    @Override
    public User findUserByEmail(String email) {
        List<User> data = load();
        return data.stream()
                .filter(user -> user.getEmail().equals(email))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }

    //특정 채널에 참가한 사용자 리스트 조회
    @Override
    public List<User> findUsersByChannel(UUID channelId) {
        List<User> data = load();
        return data.stream().filter(user -> user.getChannels().stream()
                        .anyMatch(channel -> channel.getId().equals(channelId)))
                .toList();
    }

    @Override
    public List<User> findAllUser() {
        return load();
    }

    @Override
    public User update(UUID userId, String password, String username, String email) {
        List<User> data = load();
        existsByEmail(data, email);

        User user = findInList(data, userId);
        validatePassword(user, password);

        Optional.ofNullable(username).ifPresent(user::updateUsername);
        Optional.ofNullable(email).ifPresent(user::updateEmail);

        saveOrUpdate(user);
        return user;
    }

    @Override
    public User updatePassword(UUID userId, String currentPassword, String newPassword) {
        List<User> data = load();
        User user = findInList(data, userId);
        validatePassword(user, currentPassword);
        user.updatePassword(newPassword);
        saveOrUpdate(user);
        return user;
    }

    @Override
    public void delete(UUID userId, String password) {
        List<User> data = load();
        User user = findInList(data, userId);
        validatePassword(user, password);

        user.getChannels().forEach(channel -> {
            channel.leave(user);
            user.leave(channel);

        });
        data.remove(user);
        save(data);
    }

    @Override
    public void saveOrUpdate(User user) {
        List<User> data = load();
        data.removeIf(u -> u.getId().equals(user.getId()));
        data.add(user);
        save(data);
    }


    //유저 이메일 중복체크
    private void existsByEmail(List<User> data, String email) {
        boolean exist = data.stream().anyMatch(user -> user.getEmail().equals(email));
        if (exist) {
            throw new IllegalArgumentException("이미 사용중인 이메일입니다: " + email);
        }
    }

    //비밀번호 검증
    private void validatePassword(User user, String inputPassword) {
        if (!user.getPassword().equals(inputPassword)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }

    //내부에서 수정, 삭제를 위한 조회메서드
    private User findInList(List<User> data, UUID userId) {
        return data.stream()
                .filter(user -> user.getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
    }

    private void init(Path directory) {
        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void save(List<User> data) {
        try (
                FileOutputStream fos = new FileOutputStream(filePath.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<User> load() {
        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }

        try (
                FileInputStream fis = new FileInputStream(filePath.toFile());
                ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            return (List<User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
