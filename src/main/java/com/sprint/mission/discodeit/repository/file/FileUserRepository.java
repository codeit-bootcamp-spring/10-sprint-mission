package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileUserRepository implements UserRepository {

    private final Path DIRECTORY;

    public FileUserRepository(Path DIRECTORY) {
        this.DIRECTORY = DIRECTORY;
        try {
            Files.createDirectories(DIRECTORY);
        } catch (IOException e) {
            throw new RuntimeException("유저 저장 폴더 생성 실패", e);
        }
    }

    private Path getFilePath(UUID id) {
        return DIRECTORY.resolve(id.toString() + ".ser");
    }

    @Override
    public User save(User user) {
        Path filePath = getFilePath(user.getId());
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(filePath))) {
            oos.writeObject(user);
        } catch (IOException e) {
            throw new RuntimeException("유저 저장 실패: " + user.getId(), e);
        }
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        Path filePath = getFilePath(id);
        if (!Files.exists(filePath)) {
            return Optional.empty();
        }
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(filePath))){
            User user = (User) ois.readObject();
            return  Optional.ofNullable(user);

        } catch (Exception e) {
            throw new RuntimeException("유저 로드 실패: " + id,e);
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return findAll().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        try(Stream<Path> stream = Files.list(DIRECTORY)){
            return stream
                    .filter(path -> path.toString().endsWith(".ser"))
                    .map(path -> {
                        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(path))){
                            return (User) ois.readObject();

                        } catch (Exception e) {
                            throw new RuntimeException("유저 로드 실패: ",e);
                            // 현재는 하나라도 실패하면 예외처리
                            // 만약 예외 처리된 파일 빼고 불러오고 싶다면
                            // return Optional.<User>empty();
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()); // 예외 파일 체크 안할거면 filter 적용안하고 바로 .toList() 하면됨.
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    @Override
    public boolean existsById(UUID id) {
        return Files.exists(getFilePath(id));
    }

    @Override
    public void deleteById(UUID id) {
        try {
            Files.deleteIfExists(getFilePath(id));
        } catch (IOException e) {
            throw new RuntimeException("유저 삭제 실패: " + id, e);
        }

    }
}
