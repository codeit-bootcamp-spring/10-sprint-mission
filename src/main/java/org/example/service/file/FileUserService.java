package org.example.service.file;

import org.example.entity.Channel;
import org.example.entity.Status;
import org.example.entity.User;
import org.example.exception.NotFoundException;
import org.example.service.MessageService;
import org.example.service.UserService;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileUserService implements UserService {

    private static final Path FILE_PATH = Paths.get("data", "users.ser");
    private MessageService messageService;

    public FileUserService() {
        initializeFile();
    }

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }
    // ============================================
    // 파일 I/O 기본 구조
    // ============================================

    private void initializeFile() {
        try {
            if (Files.notExists(FILE_PATH.getParent())) {
                Files.createDirectories(FILE_PATH.getParent());
            }
            if (Files.notExists(FILE_PATH)) {
                saveToFile(new HashMap<>());
            }
        } catch (IOException e) {
            throw new RuntimeException("파일 초기화 실패", e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, User> loadFromFile() {
        if (Files.notExists(FILE_PATH)) {
            return new HashMap<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(FILE_PATH))) {
            return (Map<UUID, User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("유저 데이터 로드 실패", e);
        }
    }

    private void saveToFile(Map<UUID, User> data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(FILE_PATH))) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException("유저 데이터 저장 실패", e);
        }
    }

    // ============================================
    // CRUD (구현 필요)
    // ============================================

    @Override
    public User create(String username, String email, String password, String nickname) {
        User user = new User(username, email, password, nickname);

        Map<UUID, User> data = loadFromFile();
        data.put(user.getId(), user);
        saveToFile(data);

        return user;
    }

    @Override
    public User findById(UUID userId) {
        Map<UUID, User> data = loadFromFile();
        return Optional.ofNullable(data.get(userId))
                .orElseThrow(() -> new NotFoundException("id", "존재하는 유저", userId));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(loadFromFile().values());
    }

    @Override
    public User update(UUID userId, String username, String email, String nickname, String password, Status status) {
        Map<UUID, User> data = loadFromFile();
        User user = Optional.ofNullable(data.get(userId))
                .orElseThrow(() -> new NotFoundException("id", "존재하는 유저", userId));

        Optional.ofNullable(username).ifPresent(user::updateUsername);
        Optional.ofNullable(email).ifPresent(user::updateEmail);
        Optional.ofNullable(nickname).ifPresent(user::updateNickname);
        Optional.ofNullable(password).ifPresent(user::updatePassword);
        Optional.ofNullable(status).ifPresent(user::updateStatus);

        saveToFile(data);
        return user;
    }

    @Override
    public void softDelete(UUID userId) {
        Map<UUID, User> data = loadFromFile();
        User user = Optional.ofNullable(data.get(userId))
                .orElseThrow(() -> new NotFoundException("id", "존재하는 유저", userId));

        user.updateStatus(Status.DELETED);

        saveToFile(data);
    }

    @Override
    public void hardDelete(UUID userId) {
        Map<UUID,User> data = loadFromFile();
        User user = Optional.ofNullable(data.get(userId))
                .orElseThrow(() -> new NotFoundException("id", "존재하는 유저", userId));

        user.getChannels().forEach(channel -> channel.getMembers().remove(user));

        // 메시지 완전 삭제
        new ArrayList<>(user.getMessages()).forEach(message ->
                messageService.hardDelete(message.getId())
        );

        data.remove(userId);
        saveToFile(data);
    }

    @Override
    public List<Channel> findChannelByUser(UUID userId) {
        Map<UUID, User> data = loadFromFile();
        return Optional.ofNullable(data.get(userId))
                .map(User::getChannels)
                .orElse(List.of());
    }

}
