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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(prefix = "discodeit.repository", name = "type", havingValue = "jcf", matchIfMissing = true)
public class FileUserRepository implements UserRepository {
    private final Path userPath;

    public FileUserRepository(
            @Value("${discodeit.repository.file-directory:data}") String rootPath
    ) {
        this.userPath = Paths.get(rootPath, "users");
    }

    @Override
    public Optional<User> findById(UUID userId) {
        Path userPath = getUserPath(userId);
        if (!Files.exists(userPath)) {
            return Optional.empty();
        }

        try (
                FileInputStream fis = new FileInputStream(userPath.toFile());
                ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            return Optional.ofNullable((User) ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("유저를 가져오는데 실패했습니다.");
        }
    }

    @Override
    public Optional<User> findByName(String userName) {
        return findAll().stream()
                .filter(u -> u.getUserName().equals(userName))
                .findFirst();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return findAll().stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        Path userPath = Path.of("./users");
        if(Files.exists(userPath)) {
            try {
                List<User> users = Files.list(userPath)
                        .map(path -> {
                            try(
                                    FileInputStream fis = new FileInputStream(path.toFile());
                                    ObjectInputStream ois = new ObjectInputStream(fis)
                            ) {
                                User user = (User) ois.readObject();
                                return user;
                            } catch (IOException | ClassNotFoundException e) {
                                throw new RuntimeException("모든 유저를 가져오는데 실패했습니다.");
                            }
                        })
                        .toList();
                return users;
            } catch (IOException e) {
                throw new RuntimeException("모든 유저를 가져오는데 실패했습니다.");
            }
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public List<User> findAllByChannelId(UUID channelId) {
        return findAll().stream()
                .filter(u -> u.getChannelIds()
                        .stream()
                        .anyMatch(findChannelId -> findChannelId.equals(channelId)))
                .toList();
    }

    @Override
    public void save(User user) {
        Path userPath = getUserPath(user.getId());
        try (
                FileOutputStream fos = new FileOutputStream(userPath.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(user);
        } catch (IOException e) {
            throw new RuntimeException("유저를 저장하는데 실패했습니다.");
        }
    }

    @Override
    public void deleteById(UUID userId) {
        Path userPath = getUserPath(userId);
        try {
            Files.deleteIfExists(userPath);
        } catch (IOException e) {
            throw new RuntimeException("유저를 삭제하는데 실패했습니다.");
        }
    }

    private Path getUserPath(UUID userId) {
        try {
            Files.createDirectories(userPath);
        } catch (IOException e) {
            throw new IllegalStateException("binary-contents 경로를 만드는데 실패했습니다.");
        }

        return userPath.resolve(userId.toString() + ".ser");
    }
}
