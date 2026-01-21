package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FileUserRepository implements UserRepository {
    private final Map<UUID, User> data; // 빠른 조회를 위한 컬렉션
    private final Path userDir;

    public FileUserRepository(Path userDir) {
        this.data = new HashMap<>();
        this.userDir = userDir;
        try {
            // 파일이 저장될 디렉토리가 존재하지 않을 경우 폴더 생성
            Files.createDirectories(userDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 생성 시 디렉토리와 컬렉션 동기화
        loadAllFromFiles();
    }

    private Path userFilePath(UUID userId) {
        // 유저를 구분하기 위한 파일 경로 생성
        return userDir.resolve("user-" + userId +".ser");
    }

    @Override
    public User saveUser(User user) {
        // 경로 생성 (user-id.ser)
        Path filePath = userFilePath(user.getId());

        try(FileOutputStream fos = new FileOutputStream(filePath.toFile());
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
    public User findUserById(UUID userId) {
        User user = data.get(userId);
        if (user == null) {
            throw new RuntimeException("유저가 존재하지 않습니다.");
        }
        return user;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void deleteUser(UUID userId) {
        deleteFileAndRemoveFromData(userId);
    }

    public User loadUserFile(UUID userId) {
        // 경로 생성 (user-id.ser)
        Path filePath = userFilePath(userId);

        // 파일 존재 여부 확인
        if (!Files.exists(filePath)) {
            throw new RuntimeException("유저 파일이 존재하지 않습니다.");
        }

        try (FileInputStream fis = new FileInputStream(filePath.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            // 파일 조회 후 컬렉션과 동기화
            User user = (User) ois.readObject();
            data.put(user.getId(), user);
            return user;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("유저 파일 로딩을 실패했습니다.");
        }
    }

    private void loadAllFromFiles() {
        try {
            Files.list(userDir)
                    .filter(path -> path.getFileName().toString().startsWith("user-")) // 파일명이 "user-"로 시작해야 함
                    .filter(path -> path.getFileName().toString().endsWith(".ser")) // 파일의 확장자가 ".ser"이어야 함
                    .forEach(path -> {
                        try (FileInputStream fis = new FileInputStream(path.toFile());
                             ObjectInputStream ois = new ObjectInputStream(fis)) {
                            // 파일 조회 후 컬렉션에 저장
                            User user = (User) ois.readObject();
                            data.put(user.getId(), user);
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException("유저 파일 로딩을 실패했습니다.");
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException("유저 디렉토리 조회를 실패했습니다.");
        }
    }

    private void deleteFileAndRemoveFromData(UUID userId) {
        // 경로 생성 (user-id.ser)
        Path filePath = userFilePath(userId);

        try {
            // 파일이 존재한다면 삭제 후 true 반환
            boolean deleted = Files.deleteIfExists(filePath);
            if (!deleted) {
                throw new RuntimeException("유저가 존재하지 않습니다.");
            }
            // 컬렉션에서도 삭제
            data.remove(userId);
        } catch (IOException e) {
            throw new RuntimeException("유저 파일 삭제를 실패했습니다.");
        }
    }
}
