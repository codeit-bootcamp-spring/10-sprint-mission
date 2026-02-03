package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.IntStream;

@Repository
@ConditionalOnProperty(
        prefix = "discodeit.repository",
        name = "type",
        havingValue = "file"
)
public class FileUserStatusRepository implements UserStatusRepository {
    private static final String FILE_NAME = "userStatus.ser";
    private final Path filePath;
    private final List<UserStatus> data;

    public FileUserStatusRepository(
            @Value("${discodeit.repository.file-directory:.discodeit}")  String dir
    ) {
        this.filePath = Paths.get(dir, FILE_NAME);
        this.data = loadUserStatus();
    }

    private void saveUserStatus() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
            oos.writeObject(this.data);
            System.out.println("직렬화 완료: userStatus.ser에 저장되었습니다.");
        } catch (IOException e) {
            throw new RuntimeException("userStatus 저장 중 오류 발생", e);
        }
    }

    @SuppressWarnings("unchecked")
    private List<UserStatus> loadUserStatus() {
        if (!filePath.toFile().exists() || filePath.toFile().length() == 0) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath.toFile()))) {
            Object data = ois.readObject();
            System.out.println("역직렬화 완료: " + data);
            return (List<UserStatus>) data;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("UserStatus 로드 중 오류 발생", e);
        }
    }

    @Override
    public UserStatus save(UserStatus userStatus) {
        OptionalInt indexOpt = IntStream.range(0, this.data.size())
                .filter(i -> data.get(i).getId().equals(userStatus.getId()))
                .findFirst();
        if (indexOpt.isPresent()) {
            data.set(indexOpt.getAsInt(), userStatus);
        } else {
            data.add(userStatus);
        }
            saveUserStatus();
            return userStatus;

    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        return data.stream()
                .filter(rs -> rs.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<UserStatus> findAll() {
        return new ArrayList<>(data);
    }

    @Override
    public void deleteById(UUID id) {
        data.removeIf(us -> us.getId().equals(id));
        saveUserStatus();
    }
}
