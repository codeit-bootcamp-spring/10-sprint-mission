package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

@Slf4j
@Repository
@ConditionalOnProperty(
        name = "discodeit.repository.type",
        havingValue = "file"
)
public class FileBinaryContentRepository implements BinaryContentRepository {

    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";
    String className = this.getClass().getSimpleName();

    public FileBinaryContentRepository(@Value("${discodeit.repository.file-directory}") String directory) {
        String currentDir = System.getProperty("user.dir");
        this.DIRECTORY = Paths.get(currentDir, directory, className);

        if(Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(this.DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException(className +"폴더를 생성할 수 없습니다.");
            }
        }
    }

    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public BinaryContent save(BinaryContent BinaryContent) {
        Path path = resolvePath(BinaryContent.getId());
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(path))) {
            oos.writeObject(BinaryContent);
        } catch (IOException e) {
            throw new RuntimeException(className + "파일 생성 실패", e);
        }
        return BinaryContent;
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        BinaryContent binaryContent = null;
        Path path = resolvePath(id);
        if(Files.notExists(path)) {
            try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(path))) {
                binaryContent = (BinaryContent) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(className + "파일 읽기 실패", e);
            }
        }
        return Optional.ofNullable(binaryContent);
    }

    @Override
    public List<BinaryContent> findAll() {
        try(Stream<Path> fileList = Files.list(DIRECTORY)) { // 경로 내 모든 파일 load
            return fileList
                    .filter(path -> path.toString().endsWith(EXTENSION)) // .ser로 끝나는 파일만 가져옴
                    .map(path -> {
                        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(path))){
                            return (BinaryContent) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            log.warn("Failed to load BinaryContent from file: {}", path, e); // log대신 예외 발행하면 전체 실패되버림
                            return null; // null은 읽지 못한 파일을 의미한다. 개선이 필요한 부분임
                        }
                    })
                    .filter(Objects::nonNull)
                    .toList(); // 읽는데 성공한 파일만 가져옴
        } catch (IOException e) {
            throw new RuntimeException(className + "파일 전체를 불러오는데 실패하였습니다.", e);
        }
    }

    @Override
    public List<BinaryContent> findAllByIn(List<UUID> ids) {
        return findAll().stream()
                .filter(content -> ids.contains(content.getId()))
                .toList();
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
            throw new RuntimeException(className + "파일을 삭제하는데 실패하였습니다.", e);
        }

    }
}
