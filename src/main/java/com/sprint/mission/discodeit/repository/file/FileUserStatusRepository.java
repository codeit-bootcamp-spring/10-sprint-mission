package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileUserStatusRepository implements UserStatusRepository {

    private final Path dirPath;

    public FileUserStatusRepository(@Value("${discodeit.repository.file-directory}") String dir) {
        this.dirPath = Paths.get(dir + "/userstatuses");
        init();
    }

    private void init() {
        if (!Files.exists(dirPath)) {
            try {
                Files.createDirectories(dirPath);
            } catch (IOException e) {
                throw new RuntimeException("UserStatus 데이터 폴더 생성 실패", e);
            }
        }
    }

    @Override
    public UserStatus save(UserStatus userStatus) {
        writeToFile(userStatus);
        return userStatus;
    }

    @Override
    public Optional<UserStatus> findById(UUID userStatusId) {
        Path path = dirPath.resolve(userStatusId + ".ser");
        if (!Files.exists(path)) {
            return Optional.empty();
        }
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(path))){
            return Optional.ofNullable((UserStatus) ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("UserStatus 데이터 조회 실패", e);
        }
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return findAll().stream()
                .filter(userStatus -> userStatus.getUserId().equals(userId))
                .findFirst();
    }

    @Override
    public List<UserStatus> findAll() {
        if (!Files.exists(dirPath)) {
            return List.of();
        }
        try (Stream<Path> stream= Files.list(dirPath)){
            return stream
                    .map(path -> {
                        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(path))) {
                            return (UserStatus) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException("UserStatus 데이터 조회 실패", e);
                        }
                    }).toList();
        } catch (IOException e) {
            throw new RuntimeException("UserStatus 데이터 목록 조회 실패", e);
        }
    }

    @Override
    public void delete(UserStatus userStatus) {
        Path path = dirPath.resolve(userStatus.getId() + ".ser");
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException("UserStatus 데이터 삭제 실패", e);
        }
    }

    private void writeToFile(UserStatus userStatus) {
        Path path = dirPath.resolve(userStatus.getId() + ".ser");
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(path))) {
            oos.writeObject(userStatus);
        } catch (IOException e) {
            throw new RuntimeException("UserStatus 데이터 저장 실패", e);
        }
    }
}
