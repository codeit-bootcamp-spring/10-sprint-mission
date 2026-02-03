package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.util.SerializedFileUtils;
import org.springframework.stereotype.Repository;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class FileUserRepository implements UserRepository {
    private static final String FILE_PREFIX = "user";
    private static final String ENTITY_NAME = "유저";

    private final Map<UUID, User> data; // 빠른 조회를 위한 컬렉션
    private final Path userDir;

    public FileUserRepository(Path baseDir) {
        this.data = new HashMap<>();
        this.userDir = baseDir.resolve(FILE_PREFIX);
        try {
            // 파일이 저장될 디렉토리가 존재하지 않을 경우 폴더 생성
            Files.createDirectories(userDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 생성 시 디렉토리와 컬렉션 동기화
        loadAllFromDirectory();
    }

    private Path userFilePath(UUID userId) {
        // 유저를 구분하기 위한 파일 경로 생성
        return userDir.resolve(FILE_PREFIX + "-" + userId + ".ser");
    }

    @Override
    public User save(User user) {
        // 경로 생성 (user-id.ser)
        Path filePath = userFilePath(user.getId());

        try (FileOutputStream fos = new FileOutputStream(filePath.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            // 파일 저장
            oos.writeObject(user);
            data.put(user.getId(), user);
            return user;
        } catch (IOException e) {
            throw new RuntimeException("유저 파일 저장을 실패했습니다.");
        }
    }

    @Override
    public Optional<User> findById(UUID userId) {
        return Optional.ofNullable(data.get(userId));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void delete(UUID userId) {
        if (!data.containsKey(userId)) {
            throw new RuntimeException("유저가 존재하지 않습니다.");
        }

        Path filePath = userFilePath(userId);
        SerializedFileUtils.deleteFileOrThrow(filePath, ENTITY_NAME);
        data.remove(userId);
    }

    @Override
    public boolean existsByEmail(String email) {
        // 유저 목록을 순회하며 이메일이 존재하는지 확인
        return data.values().stream()
                .anyMatch(user -> email.equals(user.getEmail()));
    }

    @Override
    public boolean existsByNickname(String nickname) {
        // 유저 목록을 순회하며 닉네임이 존재하는지 확인
        return data.values().stream()
                .anyMatch(user -> nickname.equals(user.getNickname()));
    }

    @Override
    public boolean existsByNicknameExceptUserId(String nickname, UUID exceptUserId) {
        // 유저 목록을 순회하며 닉네임은 일치하지만 id는 다른 유저가 있는지 확인
        return data.values().stream()
                .anyMatch(user ->
                        nickname.equals(user.getNickname()) &&
                                !user.getId().equals(exceptUserId));
    }

    @Override
    public Optional<User> findByEmailAndPassword(String email, String password) {
        return data.values().stream()
                .filter(user -> email.equals(user.getEmail()) &&
                        password.equals(user.getPassword()))
                .findFirst();
    }

    public User loadByIdFromFile(UUID userId) {
        // 경로 생성 (user-id.ser)
        Path filePath = userFilePath(userId);
        // 파일 역직렬화
        User user = (User) SerializedFileUtils.deserialize(filePath, ENTITY_NAME);
        // 컬렉션과 동기화
        data.put(user.getId(), user);
        return user;
    }

    private void loadAllFromDirectory() {
        data.clear();

        for (Object object : SerializedFileUtils.deserializeAll(userDir, FILE_PREFIX, ENTITY_NAME)) {
            User user = (User) object;
            data.put(user.getId(), user);
        }
    }
}
