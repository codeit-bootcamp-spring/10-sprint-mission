package com.sprint.mission.discodeit.userstatus.repository;

import com.sprint.mission.discodeit.userstatus.UserStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(prefix = "discodeit.repository", name = "type", havingValue = "file")
public class FileUserStatusRepository implements UserStatusRepository {
    private final Path userStatusPath;

    public FileUserStatusRepository(
            @Value("${discodeit.repository.file-directory:data}") String rootPath
    ) {
        userStatusPath = Paths.get(rootPath, "user-statuses");
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        Path userStatusPath = getUserStatusPath(id);
        return Optional.ofNullable(read(userStatusPath));
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return findAll().stream()
                .filter(us -> us.getUserId().equals(userId))
                .findFirst();
    }

    @Override
    public List<UserStatus> findAll() {
        if(Files.exists(userStatusPath)) {
            try {
                return Files.list(userStatusPath)
                        .map(this::read)
                        .toList();
            } catch (IOException e) {
                throw new RuntimeException("UserStatus 파일 목록을 불러오는데 실패했습니다.");
            }
        } else {
            return List.of();
        }
    }

    @Override
    public void save(UserStatus userStatus) {
        Path userStatusPath = getUserStatusPath(userStatus.getId());
        write(userStatus, userStatusPath);
    }

    @Override
    public void deleteById(UUID id) {
        Path userStatusPath = getUserStatusPath(id);
        try {
            Files.delete(userStatusPath);
        } catch (IOException e) {
            throw new RuntimeException("UserStatus 파일을 삭제하는데 실패했습니다.");
        }
    }

    @Override
    public void deleteByUserId(UUID userId) {
        UserStatus userStatus = findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 사용자를 찾을 수 없습니다."));
        deleteById(userStatus.getId());
    }

    private UserStatus read(Path path) {
        if (!Files.exists(path))
            throw new IllegalStateException("해당 파일이 존재하지 않습니다.");

        try (FileInputStream fis = new FileInputStream(path.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (UserStatus) ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("파일을 UserStatus로 변환할 수 없습니다.");
        } catch (IOException e) {
            throw new RuntimeException("UserStatus 파일이나 경로를 불러오는데 실패했습니다.");
        }
    }

    private void write(UserStatus userStatus, Path path) {
        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(userStatus);
        } catch (IOException e) {
            throw new RuntimeException("UserStatus를 파일로 저장하는데 실패했습니다.");
        }
    }

    private Path getUserStatusPath(UUID userId) {
        try {
            Files.createDirectories(userStatusPath);
        } catch (IOException e) {
            throw new IllegalStateException("user-statuses 경로를 만드는데 실패했습니다.");
        }
        return userStatusPath.resolve(userId.toString() + ".ser");
    }
}
