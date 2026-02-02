package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
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
public class FileUserRepository implements UserRepository {

    private final Path dirPath;

    public FileUserRepository(@Value("${discodeit.repository.file-directory}") String dir) {
        this.dirPath = Paths.get(dir + "/users");
        init();
    }

    private void init() {
        if (!Files.exists(dirPath)) {
            try {
                Files.createDirectories(dirPath);
            } catch (IOException e) {
                throw new RuntimeException("유저 데이터 폴더 생성 실패", e);
            }
        }
    }

    @Override
    public User save(User user) {
        writeToFile(user);
        return user;
    }

    @Override
    public Optional<User> findById(UUID userId) {
        Path path = dirPath.resolve(userId + ".ser");
        if (!Files.exists(path)) {
            return Optional.empty();
        }
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(path))){
            return Optional.ofNullable((User) ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("유저 데이터 조회 실패", e);
        }
    }

    @Override
    public List<User> findAll() {
        if (!Files.exists(dirPath)) {
            return List.of();
        }
        try (Stream<Path> stream = Files.list(dirPath)) {
            return stream
                    .map(path -> {
                        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(path))) {
                            return (User) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException("유저 데이터 조회 실패", e);
                        }
                    })
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("유저 데이터 목록 조회 실패", e);
        }
    }

    @Override
    public void delete(User user) {
        Path path = dirPath.resolve(user.getId() + ".ser");
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException("유저 데이터 삭제 실패", e);
        }
    }

    private void writeToFile(User user) {
        Path path = dirPath.resolve(user.getId() + ".ser");
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(path))) {
            oos.writeObject(user);
        } catch (IOException e) {
            throw new RuntimeException("유저 데이터 저장 실패", e);
        }
    }
}
