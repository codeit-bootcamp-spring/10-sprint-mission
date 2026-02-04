package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
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
@ConditionalOnProperty(prefix = "discodeit.repository", name = "type", havingValue = "jcf", matchIfMissing = true)
public class FileBinaryContentRepository implements BinaryContentRepository {
    private final Path binaryContentPath;

    public FileBinaryContentRepository(
            @Value("${discodeit.repository.file-directory:data}") String rootPath
    ) {
        this.binaryContentPath = Paths.get(rootPath, "binary-contents");
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        Path binaryContentPath = getBinaryContentPath(id);
        return Optional.ofNullable(read(binaryContentPath));
    }

    @Override
    public List<BinaryContent> findAll() {
        Path binaryContentPath = Paths.get("binary-contents");
        if(!Files.exists(binaryContentPath)) {
            try {
                return Files.list(binaryContentPath)
                        .map(this::read)
                        .toList();
            } catch (IOException e) {
                throw new RuntimeException("BinaryContent 파일 목록을 불러오는데 실패했습니다.");
            }
        } else {
            return List.of();
        }
    }

    @Override
    public void save(BinaryContent binaryContent) {
        Path binaryContentPath = getBinaryContentPath(binaryContent.getId());
        write(binaryContent, binaryContentPath);
    }

    @Override
    public void deleteById(UUID id) {
        Path binaryContentPath = getBinaryContentPath(id);
        try {
            Files.delete(binaryContentPath);
        } catch (IOException e) {
            throw new RuntimeException("BinaryContent 파일을 삭제하는데 실패했습니다.");
        }
    }

    private BinaryContent read(Path path) {
        if (!Files.exists(path))
            throw new IllegalStateException("BinaryContent 파일이나 경로가 이미 존재합니다.");

        try (FileInputStream fis = new FileInputStream(path.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (BinaryContent) ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("파일을 BinaryContent로 변환할 수 없습니다.");
        } catch (IOException e) {
            throw new RuntimeException("BinaryContent 파일이나 경로를 불러오는데 실패했습니다.");
        }
    }

    private void write(BinaryContent binaryContent, Path path) {
        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(binaryContent);
        } catch (IOException e) {
            throw new RuntimeException("BinaryContent를 파일로 저장하는데 실패했습니다.");
        }
    }


    private Path getBinaryContentPath(UUID statusId) {
        try {
            Files.createDirectories(binaryContentPath);
        } catch (IOException e) {
            throw new IllegalStateException("binary-contents 경로를 만드는데 실패했습니다.");
        }

        return binaryContentPath.resolve(statusId.toString() + ".ser");
    }
}
