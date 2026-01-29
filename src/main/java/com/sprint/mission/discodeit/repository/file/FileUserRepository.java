package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class FileUserRepository implements UserRepository {

    private static final Path dirPath = Paths.get(System.getProperty("user.dir") + "/data/users");

    public FileUserRepository() {
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
        return findAll().stream()
                .filter(user -> user.getId().equals(userId))
                .findAny();
    }

    @Override
    public List<User> findAll() {
        if(!Files.exists(dirPath)) {
            return new ArrayList<>();
        }
        try {
            List<User> list = Files.list(dirPath)
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            Object data = ois.readObject();
                            return (User) data;
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.toList());
            return list;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(User user) {
        File file = new File(dirPath.toFile(), user.getId().toString() + ".ser");
        if (file.exists()) {
            if (!file.delete()) {
                throw new RuntimeException("유저 파일 삭제 실패");
            }
        }
    }

    private void writeToFile(User user) {
        File file = new File(dirPath.toFile(), user.getId().toString() + ".ser");
        try (
                FileOutputStream fos = new FileOutputStream(file);
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(user);
        } catch (IOException e) {
            throw new RuntimeException("유저 데이터 저장 실패", e);
        }
    }
}
