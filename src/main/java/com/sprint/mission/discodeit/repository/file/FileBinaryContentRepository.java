package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.util.SerializedFileUtils;
import org.springframework.stereotype.Repository;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class FileBinaryContentRepository implements BinaryContentRepository {
    private static final String FILE_PREFIX = "binaryContent";
    private static final String ENTITY_NAME = "바이너리 컨텐츠";

    private final Map<UUID, BinaryContent> data; // 빠른 조회를 위한 컬렉션
    private final Path binaryContentDir;

    public FileBinaryContentRepository(Path baseDir) {
        this.data = new HashMap<>();
        this.binaryContentDir = baseDir.resolve(FILE_PREFIX);
        try {
            // 파일이 저장될 디렉토리가 존재하지 않을 경우 폴더 생성
            Files.createDirectories(binaryContentDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 생성 시 디렉토리와 컬렉션 동기화
        loadAllFromDirectory();
    }

    private Path binaryContentFilePath(UUID binaryContentId) {
        // 바이너리 컨텐츠를 구분하기 위한 파일 경로 생성
        return binaryContentDir.resolve(FILE_PREFIX + "-" + binaryContentId + ".ser");
    }

    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        // 경로 생성 (binaryContent-id.ser)
        Path filePath = binaryContentFilePath(binaryContent.getId());

        try (FileOutputStream fos = new FileOutputStream(filePath.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            // 파일 저장
            oos.writeObject(binaryContent);
            data.put(binaryContent.getId(), binaryContent);
            return binaryContent;
        } catch (IOException e) {
            throw new RuntimeException("바이너리 컨텐츠 파일 저장을 실패했습니다.");
        }
    }

    @Override
    public Optional<BinaryContent> findById(UUID binaryContentId) {
        return Optional.ofNullable(data.get(binaryContentId));
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> binaryContentIds) {
        return binaryContentIds.stream()
                .map(data::get)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public void delete(UUID binaryContentId) {
        if (!data.containsKey(binaryContentId)) {
            throw new RuntimeException("바이너리 컨텐츠가 존재하지 않습니다.");
        }

        Path filePath = binaryContentFilePath(binaryContentId);
        SerializedFileUtils.deleteFileOrThrow(filePath, ENTITY_NAME);
        data.remove(binaryContentId);
    }

    public BinaryContent loadByIdFromFile(UUID binaryContentId) {
        // 경로 생성 (binaryContent-id.ser)
        Path filePath = binaryContentFilePath(binaryContentId);
        // 파일 역직렬화
        BinaryContent binaryContent = (BinaryContent) SerializedFileUtils.deserialize(filePath, ENTITY_NAME);
        // 컬렉션과 동기화
        data.put(binaryContent.getId(), binaryContent);
        return binaryContent;
    }

    private void loadAllFromDirectory() {
        data.clear();

        for (Object object : SerializedFileUtils.deserializeAll(binaryContentDir, FILE_PREFIX, ENTITY_NAME)) {
            BinaryContent binaryContent = (BinaryContent) object;
            data.put(binaryContent.getId(), binaryContent);
        }
    }
}
