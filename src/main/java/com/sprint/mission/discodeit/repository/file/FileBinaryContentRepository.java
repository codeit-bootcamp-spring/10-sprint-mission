package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.util.FileUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(
        name = "discodeit.repository.type" ,
        havingValue = "file"
)
public class FileBinaryContentRepository implements BinaryContentRepository {
    private final Path directory = Paths.get(System.getProperty("user.dir"), "data", "binaryContent");

    public FileBinaryContentRepository() {
        FileUtil.init(directory);
    }

    // 첨부 파일 저장
    @Override
    public void save(BinaryContent binaryContent) {
        Path filePath = Paths.get(directory.toString(), binaryContent.getId() + ".ser");
        FileUtil.save(filePath, binaryContent);
    }

    // 첨부 파일 단건 조회
    @Override
    public Optional<BinaryContent> findById(UUID binaryContentId) {
        BinaryContent binaryContent = FileUtil.loadSingle(directory.resolve(binaryContentId + ".ser"));

        return Optional.ofNullable(binaryContent);
    }

    // 첨부 파일 전체 조회
    @Override
    public List<BinaryContent> findAll() {
        return FileUtil.load(directory);
    }

    // 첨부 파일 삭제
    @Override
    public void delete(BinaryContent binaryContent) {
        try {
            Files.deleteIfExists(directory.resolve(binaryContent.getId() + ".ser"));
        } catch (IOException e) {
            throw new RuntimeException("[삭제 실패] 시스템 오류가 발생했습니다.", e);
        }
    }
}
