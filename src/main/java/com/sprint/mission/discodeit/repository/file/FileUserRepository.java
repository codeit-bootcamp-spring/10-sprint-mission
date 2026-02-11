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

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileUserRepository implements UserRepository {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    // @Value를 통해 yaml의 file-directory 값을 주입받음
    public FileUserRepository(@Value("${discodeit.repository.file-directory:.discodeit}") String directoryName) {
        // System.getProperty("user.dir") -> 현재 자바 애플리케이션이 실행되고 있는 프로젝트의 루트 경로(작업 디렉토리)를 가져 온다.
        // file-data-map -> 중간 디렉토리 이름
        // User.class.getSimpleName() -> User 클래스의 이름에서 패키지명을 뺀 순수 클래스 이름 문자열인 User를 반환.
        // Path.get은 여러 개의 문자열 조각을 운영체제에 맞는 구분자를 사용해 하나로 합쳐서 경로 객체(Path)로 만들어 준다.
        // user.dir -> 현재 작업 디렉토리(User Working Directory)를 의미하는 시스템 예약어
        DIRECTORY = Paths.get(System.getProperty("user.dir"), directoryName, User.class.getSimpleName());
        if (Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException("디렉터리 생성 실패: " + DIRECTORY, e);
            }
        }
    }

    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public User save(User user) {
        Path path = resolvePath(user.getId());
        try (
                FileOutputStream fos = new FileOutputStream(path.toFile()); // 파일에 바이트를 기록하는 통로 (어디에 저장할 것인가)
                ObjectOutputStream oos = new ObjectOutputStream(fos) // 객체를 바이트 데이터로 변환 (무엇을 어떻게 변환할 것인가)
        ) {
            oos.writeObject(user);
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패: " + user.getId(), e);
        }
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        User userNullable = null;
        Path path = resolvePath(id);
        if (Files.exists(path)) {
            try (
                    FileInputStream fis = new FileInputStream(path.toFile());
                    ObjectInputStream ois = new ObjectInputStream(fis)
            ) {
                userNullable = (User) ois.readObject(); // 다운캐스팅
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException("파일 읽기 실패: " + id, e);
            }
        }
        return Optional.ofNullable(userNullable);
    }

    @Override
    public List<User> findAll() {
        try {
            return Files.list(DIRECTORY)
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> { // map ->
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            return (User) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException("파일 읽기 중 오류 발생: " + path, e);
                        }
                    })
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("파일 목록 조회 실패", e);
        }
    }

    @Override
    public boolean existsById(UUID id) {
        Path path = resolvePath(id);
        return Files.exists(path);
    }

    @Override
    public void deleteById(UUID id) {
        Path path = resolvePath(id);
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException("파일 삭제 실패: " + id, e);
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        return findAll().stream()
                .anyMatch(user -> user.getUsername().equals(username));
    }

    @Override
    public boolean existsByEmail(String email) {
        return findAll().stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }
}
