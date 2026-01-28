package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileUserRepository implements UserRepository {
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
                .filter(u -> u.getChannels().stream().anyMatch(c -> c.getId().equals(channelId)))
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
        return Paths.get("users", userId.toString() + ".ser");
    }
}
