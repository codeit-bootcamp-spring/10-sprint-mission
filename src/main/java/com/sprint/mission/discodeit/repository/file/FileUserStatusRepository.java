package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.stereotype.Repository;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class FileUserStatusRepository implements UserStatusRepository {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";
    String className = this.getClass().getSimpleName();

    public FileUserStatusRepository() {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map", className);
        if (Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException("폴더를 생성하지 못했습니다.");
            }
        }
    }

    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public UserStatus save(UserStatus userStatus) {
        Path path = resolvePath(userStatus.getId());
        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(userStatus);
        } catch (IOException e) {
            throw new RuntimeException("파일 생성에 실패했습니다.");
        }
        return userStatus;
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        if (id == null) {
            return Optional.empty();
        }
        Path path = resolvePath(id);

        if (Files.notExists(path)) {
            return Optional.empty();
        }

        try (
                FileInputStream fis = new FileInputStream(path.toFile());
                ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            return Optional.ofNullable((UserStatus) ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("파일읽기 실패");
        }
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        if (userId == null) {
            return Optional.empty();
        }

        return findAll().stream()
                .filter(us -> userId.equals(us.getUserId()))
                .findFirst();
    }

    @Override
    public List<UserStatus> findAll() {
        try {
            return Files.list(DIRECTORY) // 디렉터리 내 파일 스트림
                    .filter(path -> path.toString().endsWith(EXTENSION)) // .ser만 대상으로
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            return (UserStatus) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean existsById(UUID id) {
        if (id == null) {
            return false;
        }
        return Files.exists(resolvePath(id));
    }

    @Override
    public boolean existsByUserId(UUID userId) {
        if (userId == null) {
            return false;
        }
        return findAll().stream().anyMatch(us -> userId.equals(us.getUserId()));
    }

    @Override
    public void deleteById(UUID id) {
        if (id == null) {
            return;
        }
        Path path = resolvePath(id);

        if (Files.notExists(path)) {
            return;
        }

        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteByUserId(UUID userId) {
        // 입력 방어
        if (userId == null) {
            return;
        }
        findByUserId(userId).ifPresent(us -> deleteById(us.getId()));
    }

}