package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.util.FileUtil;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class FileUserRepository implements UserRepository {
    private final Path directory = Paths.get(System.getProperty("user.dir"), "data", "users");      // 경로 설정

    public FileUserRepository() {
        FileUtil.init(directory);
    }

    // 사용자 저장
    @Override
    public void save(User user) {
        Path filePath = Paths.get(directory.toString(), user.getId() + ".ser");
        FileUtil.save(filePath, user);
    }

    // 사용자 단건 조회
    @Override
    public Optional<User> findById(UUID userId) {
        User targetUser = FileUtil.loadSingle(directory.resolve(userId + ".ser"));

        return Optional.ofNullable(targetUser);
    }

    // 사용자 전체 조회
    @Override
    public List<User> findAll() {
        return FileUtil.load(directory);
    }

    // 사용자 삭제
    @Override
    public void delete(User user) {
        try {
            Files.deleteIfExists(directory.resolve(user.getId() + ".ser"));
        } catch (IOException e) {
            throw new RuntimeException("[삭제 실패] 시스템 오류가 발생했습니다.", e);
        }
    }

    // 유효성 검증 (이메일 중복)
    @Override
    public boolean existsByEmail(String email) {
        return findAll().stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }

    // 유효성 검증 (이름 중복)
    @Override
    public boolean existsByNickname(String nickname) {
        return findAll().stream()
                .anyMatch(user -> user.getNickname().equals(nickname));
    }
}
