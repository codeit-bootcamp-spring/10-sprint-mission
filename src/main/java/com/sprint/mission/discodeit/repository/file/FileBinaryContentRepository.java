package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileBinaryContentRepository implements BinaryContentRepository {

    private final Path dirPath;

    public FileBinaryContentRepository(@Value("${discodeit.repository.file-directory}") String dir) {
        this.dirPath = Paths.get(dir + "/binarycontents");
        init();
    }

    private void init() {
        if (!Files.exists(dirPath)) {
            try {
                Files.createDirectories(dirPath);
            } catch (IOException e) {
                throw new RuntimeException("BinaryContent 데이터 폴더 생성 실패", e);
            }
        }
    }

    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        writeToFile(binaryContent);
        return binaryContent;
    }

    @Override
    public Optional<BinaryContent> findById(UUID binaryContentId) {
        Path path = dirPath.resolve(binaryContentId + ".ser");
        if (!Files.exists(path)) {
            return Optional.empty();
        }
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(path))) {
            return Optional.ofNullable((BinaryContent) ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("BinaryContent 파일 조회 실패", e);
        }
    }

    @Override
    public List<BinaryContent> findAll() {
        if (!Files.exists(dirPath)) {
            return List.of();
        }
        try (Stream<Path> stream = Files.list(dirPath)) {
            return stream
                    .map(path -> {
                        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(path))) {
                            return (BinaryContent) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException("BinaryContent 파일 조회 실패", e);
                        }
                    })
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("BinaryContent 파일 목록 조회 실패", e);
        }
    }

    @Override
    public void delete(BinaryContent binaryContent) {
        Path path = dirPath.resolve(binaryContent.getId() + ".ser");
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException("BinaryContent 데이터 삭제 실패", e);
        }
    }

    private void writeToFile(BinaryContent binaryContent) {
        Path path = dirPath.resolve(binaryContent.getId() + ".ser");
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(path))) {
            oos.writeObject(binaryContent);
        } catch (IOException e) {
            throw new RuntimeException("BinaryContent 데이터 저장 실패", e);
        }
    }
}
