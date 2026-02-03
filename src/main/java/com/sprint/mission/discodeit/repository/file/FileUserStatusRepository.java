package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.util.SerializedFileUtils;
import org.springframework.stereotype.Repository;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class FileUserStatusRepository implements UserStatusRepository {
    private static final String FILE_PREFIX = "userStatus";
    private static final String ENTITY_NAME = "유저 상태";

    private final Map<UUID, UserStatus> data; // 빠른 조회를 위한 컬렉션
    private final Path userStatusDir;

    public FileUserStatusRepository(Path baseDir) {
        this.data = new HashMap<>();
        this.userStatusDir = baseDir.resolve(FILE_PREFIX);
        try {
            // 파일이 저장될 디렉토리가 존재하지 않을 경우 폴더 생성
            Files.createDirectories(userStatusDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 생성 시 디렉토리와 컬렉션 동기화
        loadAllFromDirectory();
    }

    private Path userStatusFilePath(UUID userStatusId) {
        // 유저 상태를 구분하기 위한 파일 경로 생성
        return userStatusDir.resolve(FILE_PREFIX + "-" + userStatusId + ".ser");
    }

    @Override
    public UserStatus save(UserStatus userStatus) {
        // 경로 생성 (userStatus-id.ser)
        Path filePath = userStatusFilePath(userStatus.getId());

        try (FileOutputStream fos = new FileOutputStream(filePath.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            // 파일 저장
            oos.writeObject(userStatus);
            data.put(userStatus.getId(), userStatus);
            return userStatus;
        } catch (IOException e) {
            throw new RuntimeException("유저 상태 파일 저장을 실패했습니다.");
        }
    }

    @Override
    public Optional<UserStatus> findById(UUID userStatusId) {
        return Optional.ofNullable(data.get(userStatusId));
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return data.values().stream()
                .filter(userStatus -> userStatus.getUserId().equals(userId))
                .findFirst();
    }

    @Override
    public List<UserStatus> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void delete(UUID userStatusId) {
        if (!data.containsKey(userStatusId)) {
            throw new RuntimeException("유저 상태가 존재하지 않습니다.");
        }

        Path filePath = userStatusFilePath(userStatusId);
        SerializedFileUtils.deleteFileOrThrow(filePath, ENTITY_NAME);
        data.remove(userStatusId);
    }

    public UserStatus loadByIdFromFile(UUID userStatusId) {
        // 경로 생성 (userStatus-id.ser)
        Path filePath = userStatusFilePath(userStatusId);
        // 파일 역직렬화
        UserStatus userStatus = (UserStatus) SerializedFileUtils.deserialize(filePath, ENTITY_NAME);
        // 컬렉션과 동기화
        data.put(userStatus.getId(), userStatus);
        return userStatus;
    }

    private void loadAllFromDirectory() {
        data.clear();

        for (Object object : SerializedFileUtils.deserializeAll(userStatusDir, FILE_PREFIX, ENTITY_NAME)) {
            UserStatus userStatus = (UserStatus) object;
            data.put(userStatus.getId(), userStatus);
        }
    }
}
